package funix.tgcp.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.guide.dayoff.DayOff;
import funix.tgcp.guide.dayoff.DayOffRepository;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.GuideServiceService;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.user.User;
import funix.tgcp.util.LogHelper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class BookingService {

    private static final LogHelper logger = new LogHelper(BookingService.class);

    @Autowired private BookingRepository bookingRepo;
    @Autowired private DayOffRepository dayOffRepo;
    @Autowired private NotificationService notifiService;
    @Autowired private GuideServiceService guideServiceService;

    private void validateGuideOwnership(Booking booking, Long guideId) {
        if (booking == null || booking.getGuide() == null) {
            throw new IllegalArgumentException("Booking or guide is null");
        }

        if (!booking.getGuide().getId().equals(guideId)) {
            throw new SecurityException("Guide ID mismatch. Booking guide does not match the provided guide ID.");
        }
    }

    private void validateCustomerOwnership(Booking booking, Long customerId) {
        if (booking == null || booking.getCustomer() == null) {
            throw new IllegalArgumentException("Booking or customer is null");
        }

        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new SecurityException("Customer ID mismatch. Booking customer does not match the provided customer ID.");
        }
    }

    @Transactional
    public Optional<Booking> createBooking(Booking bookingRequest) {
        User guide = bookingRequest.getGuide();    

        if (guide == null || bookingRequest.getStartDate() == null || bookingRequest.getEndDate() == null) {
            throw new IllegalArgumentException("Invalid booking data");
        }

        GuideService requestedService = bookingRequest.getGuideService();
        Optional<GuideService> matchingService = guideServiceService.findValidGuideService(guide.getId(), requestedService);
        
        if (matchingService.isEmpty()) {
            throw new IllegalArgumentException("No matching guide service found for the specified criteria.");
        }

        GuideService selectedService = matchingService.get();
        bookingRequest.setGuideService(selectedService);

        Double totalPrice = calculateTotalPrice(
                bookingRequest.getStartDate(),
                bookingRequest.getEndDate(),
                selectedService.getPricePerDay()
        );
        bookingRequest.setTotalPrice(totalPrice);

        notifiService.sendNotification(
                guide,
                bookingRequest.getCustomer().getFullName() + " booked you, please confirm!",
                "/dashboard#manage-bookings"
        );

        return Optional.of(bookingRepo.save(bookingRequest));
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        return bookingRepo.findByCustomerId(customerId);
    }

    public List<Booking> getBookingsByGuide(Long guideId) {
        return bookingRepo.findByGuideId(guideId);
    }

    public List<Booking> findAll() {
        return bookingRepo.findAll();
    }

    public void deleteBooking(Long bookingId, Long currentUserId) {
        bookingRepo.deleteById(bookingId);
    }

    public Optional<Booking> findById(Long bookingId) {
        return bookingRepo.findById(bookingId);
    }

    @Transactional
    public void confirmBooking(Long bookingId, Long guideId) {
    	Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);       
        Booking booking = bookingOpt.get();
        
        validateGuideOwnership(booking, guideId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only bookings in PENDING status can be confirmed.");
        }

        booking.setReason(null);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepo.save(booking);

        notifiService.sendNotification(
            booking.getCustomer(),
            booking.getGuide().getFullName() + " confirmed your booking!",
            "/dashboard#booking-history"
        );

        generateAutoDayOffs(booking);
    }

    @Transactional
    public void cancelBookingByUser(Long bookingId, String reason, Long currentUserId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);       
        Booking booking = bookingOpt.get();
        
        validateCustomerOwnership(booking, currentUserId);

        if (booking.getStatus() != BookingStatus.PENDING 
                && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking cannot be canceled, invalid status.");
        }

        booking.setStatus(BookingStatus.CANCELED_BY_USER);
        booking.setReason(reason);
        booking.setCanceledAt(LocalDateTime.now());
        bookingRepo.save(booking);

        notifiService.sendNotification(
                booking.getGuide(),
                booking.getCustomer().getFullName() + " canceled booking!",
                "/dashboard#manage-bookings"
        );
    }

    @Transactional
    public void cancelBookingByGuide(Long bookingId, String reason, Long currentUserId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        Booking booking = bookingOpt.get();

        validateGuideOwnership(booking, currentUserId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking must be confirmed before it can be canceled.");
        }

        booking.setStatus(BookingStatus.CANCELED_BY_GUIDE);
        booking.setReason(reason);
        booking.setCanceledAt(LocalDateTime.now());
        bookingRepo.save(booking);

        dayOffRepo.deleteByBookingId(bookingId);

        notifiService.sendNotification(
                booking.getCustomer(),
                booking.getGuide().getFullName() + " canceled your booking!",
                "/dashboard#booking-history"
        );
    }

    @Transactional
    public void rejectBooking(Long bookingId, String reason, Long currentUserId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);      
        Booking booking = bookingOpt.get();

        validateGuideOwnership(booking, currentUserId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected.");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setReason(reason);
        bookingRepo.save(booking);

        notifiService.sendNotification(
                booking.getCustomer(),
                booking.getGuide().getFullName() + " rejected your booking!",
                "/dashboard#booking-history"
        );
    }

    @Transactional
    public void completeBooking(Long bookingId, Long currentUserId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        Booking booking = bookingOpt.get();

        validateGuideOwnership(booking, currentUserId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking must be confirmed before it can be completed.");
        }

        booking.setReason(null);
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);

        notifiService.sendNotification(
                booking.getCustomer(),
                "Your booking completed, please share feedback about your guide!",
                "/dashboard#booking-history"
        );
    }

    public long countCompletedByGuideId(Long userId) {
        return bookingRepo.countCompletedByGuideId(userId);
    }

    public long countCompletedByUserId(Long userId) {
        return bookingRepo.countCompletedByUserId(userId);
    }

    public Page<Booking> findBookingByCustomerAndFilter(Long customerId, String destination, LocalDate startDate,
                                                        LocalDate endDate, String guide, BookingStatus status, Pageable pageable) {
        return bookingRepo.findBookingByCustomerAndFilter(customerId, destination, startDate, endDate, guide, status, pageable);
    }

    public Page<Booking> findBookingByGuideAndFilter(Long guideId, String destination, LocalDate startDate,
                                                     LocalDate endDate, String user, BookingStatus status, Pageable pageable) {
        return bookingRepo.findBookingByGuideAndFilter(guideId, destination, startDate, endDate, user, status, pageable);
    }

    public int updateStatus(Long id, BookingStatus status) {
        return bookingRepo.updateStatus(id, status);
    }

    private void generateAutoDayOffs(Booking booking) {
        LocalDate start = booking.getStartDate();
        LocalDate end = booking.getEndDate();
        Long guideId = booking.getGuide().getId();

        while (!start.isAfter(end)) {
            if (!dayOffRepo.existsByGuideIdAndDate(guideId, start)) {
                DayOff date = new DayOff();
                date.setDate(start);
                date.setGuide(booking.getGuide());
                date.setAutoGenerated(true);
                date.setBookingId(booking.getId());
                dayOffRepo.save(date);
            }
            start = start.plusDays(1);
        }
    }

    private Double calculateTotalPrice(LocalDate startDate, LocalDate endDate, Double pricePerDay) {
        if (startDate != null && endDate != null && !endDate.isBefore(startDate)) {
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            return days * pricePerDay;
        } else {
            throw new IllegalArgumentException("The end date must be after or equal to the start date.");
        }
    }
}
