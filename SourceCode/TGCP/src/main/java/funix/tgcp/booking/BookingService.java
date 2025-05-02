// Refactored BookingService (no ResponseEntity in service layer)
package funix.tgcp.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import funix.tgcp.booking.payment.PaymentOption;
import funix.tgcp.guide.dayoff.DayOff;
import funix.tgcp.guide.dayoff.DayOffRepository;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.GuideServiceService;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.util.LogHelper;

@Service
public class BookingService {

    private static final LogHelper logger = new LogHelper(BookingService.class);

    @Autowired private BookingRepository bookingRepo;
    @Autowired private DayOffRepository dayOffRepo;
    @Autowired private NotificationService notifiService;
    @Autowired private GuideServiceService guideServiceService;

    @Transactional
    public Optional<Booking> createBooking(Booking bookingRequest) {
        GuideService g = bookingRequest.getGuideService();
        Optional<GuideService> gO = guideServiceService.findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(
                bookingRequest.getGuide().getId(), g.getType(), g.getGroupSizeCategory(), g.getLanguage(), g.getCity());

        if (gO.isEmpty()) return Optional.empty();

        bookingRequest.setGuideService(gO.get());       

        Booking savedBooking = bookingRepo.save(bookingRequest);
        notifiService.sendNotification(
                bookingRequest.getGuide(),
                bookingRequest.getCustomer().getFullName() + " booked you, please confirm!",
                "/dashboard#manage-bookings"
        );
        return Optional.of(savedBooking);
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

    public void deleteBooking(Long bookingId) {
        bookingRepo.deleteById(bookingId);
    }

    public Optional<Booking> findById(Long bookingId) {
        return bookingRepo.findById(bookingId);
    }

    @Transactional
    public boolean confirmBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) return false;

        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.PENDING) {
            return false;
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
        return true;
    }

    @Transactional
    public boolean cancelBookingByUser(Long bookingId, String reason) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) return false;

        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.PENDING 
        		&& booking.getStatus() != BookingStatus.CONFIRMED) {
            return false;
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
        return true;
    }

    @Transactional
    public boolean cancelBookingByGuide(Long bookingId, String reason) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) return false;

        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.CONFIRMED) 
        	return false;

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
        return true;
    }

    @Transactional
    public boolean rejectBooking(Long bookingId, String reason) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) return false;

        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.PENDING) return false;

        booking.setStatus(BookingStatus.REJECTED);
        booking.setReason(reason);
        bookingRepo.save(booking);

        notifiService.sendNotification(
                booking.getCustomer(),
                booking.getGuide().getFullName() + " rejected your booking!",
                "/dashboard#booking-history"
        );
        return true;
    }

    @Transactional
    public boolean completeBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) return false;

        Booking booking = bookingOpt.get();
        if (booking.getStatus() != BookingStatus.CONFIRMED) return false;

        booking.setReason(null);
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);

        notifiService.sendNotification(
                booking.getCustomer(),
                "Your booking completed, please share feedback about your guide!",
                "/dashboard#booking-history"
        );
        return true;
    }

    public long countCompletedByUserIdOrGuideId(Long userId) {
        return bookingRepo.countCompletedByUserIdOrGuideId(userId);
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
}
