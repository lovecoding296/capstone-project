package funix.tgcp.booking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.GuideServiceRepository;
import funix.tgcp.guide.service.GuideServiceService;
import funix.tgcp.notification.NotificationService;
import funix.tgcp.user.City;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;

@Transactional
public class BookingServiceTest {

    @Mock
    private GuideServiceRepository guideServiceRepository;

    @Mock
    private BookingRepository bookingRepo;
    
    @Mock
    private GuideServiceService guideServiceService;
    
    @Mock
    private NotificationService notifiService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // TC1: Create booking with valid service match
    @Test
    void TC1_createBookingWithValidServiceMatch() {
        User guide = new User();
        guide.setId(10000L);
        
        User customer = new User();
        guide.setId(10001L);
        
        LocalDate startDate = LocalDate.of(2025, 5, 15);
        LocalDate endDate = LocalDate.of(2025, 5, 17);
        GuideService guideService = new GuideService();
        guideService.setLanguage(Language.English);
        guideService.setCity(City.HA_NOI);
        guideService.setPricePerDay(1000000D);
        guideService.setGroupSizeCategory(GroupSizeCategory.UNDER_5);
        guideService.setGuide(guide);
        
        Booking bookingRequest = new Booking();
        bookingRequest.setGuideService(guideService);
        bookingRequest.setStartDate(startDate);
        bookingRequest.setEndDate(endDate);
        bookingRequest.setGuide(guide);
        bookingRequest.setCustomer(customer);
               
        when(guideServiceService
        		.findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(
        				guide.getId(), 
        				guideService.getType(), 
        				guideService.getGroupSizeCategory(), 
        				guideService.getLanguage(), 
        				guideService.getCity())).thenReturn(Optional.of(guideService));
        when(bookingRepo.save(bookingRequest)).thenReturn(bookingRequest);
        Optional<Booking> booking = bookingService.createBooking(bookingRequest);
        
        assertNotNull(booking.get());
        assertEquals(3000000D, booking.get().getTotalPrice());
        assertEquals(BookingStatus.PENDING, booking.get().getStatus());
        verify(bookingRepo).save(booking.get());
    }

    // TC2: Create booking with no matching GuideService
    @Test
    void TC2_createBookingWithNoMatchingService() {    	
        LocalDate startDate = LocalDate.of(2025, 5, 15);
        LocalDate endDate = LocalDate.of(2025, 5, 17);
        GuideService guideService = new GuideService();
        
        guideService.setLanguage(Language.Spanish);
        guideService.setCity(City.HA_NOI);
        guideService.setPricePerDay(1000000D);
        
        User guide = new User();
        guide.setId(10000L);
        guideService.setGuide(guide);
        
        Booking bookingRequest = new Booking();
        bookingRequest.setGuideService(guideService);
        bookingRequest.setStartDate(startDate);
        bookingRequest.setEndDate(endDate);
        

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
    }

    // TC3: Create booking with null startDate or endDate
    @Test
    void TC3_createBookingWithNullDates() {
        LocalDate startDate = null;
        LocalDate endDate = LocalDate.of(2025, 5, 17);
        GuideService guideService = new GuideService();
        guideService.setLanguage(Language.English);
        guideService.setCity(City.HA_NOI);
        guideService.setPricePerDay(1000000D);
        
        User guide = new User();
        guide.setId(10000L);
        guideService.setGuide(guide);
        
        Booking bookingRequest = new Booking();
        bookingRequest.setGuideService(guideService);
        bookingRequest.setStartDate(startDate);
        bookingRequest.setEndDate(endDate);
        
        
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
    }

    // TC4: Create booking with endDate before startDate
    @Test
    void TC4_createBookingWithEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.of(2025, 5, 15);
        LocalDate endDate = LocalDate.of(2025, 5, 14);
        GuideService guideService = new GuideService();
        guideService.setLanguage(Language.English);
        guideService.setCity(City.HA_NOI);
        guideService.setPricePerDay(1000000D);
        
        User guide = new User();
        guide.setId(10000L);
        guideService.setGuide(guide);
        
        Booking bookingRequest = new Booking();
        bookingRequest.setGuideService(guideService);
        bookingRequest.setStartDate(startDate);
        bookingRequest.setEndDate(endDate);
        
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
    }

    // TC5: Create booking across multiple days
    @Test
    void TC5_createBookingAcrossMultipleDays() {
        LocalDate startDate = LocalDate.of(2025, 5, 15);
        LocalDate endDate = LocalDate.of(2025, 5, 17);
        GuideService guideService = new GuideService();
        guideService.setLanguage(Language.English);
        guideService.setCity(City.HA_NOI);
        guideService.setPricePerDay(1000000D);
        
        User customer = new User();
        customer.setId(3L);
        customer.setFullName("User Test");
        
        User guide = new User();
        guide.setId(4L);
        guide.setFullName("Guide Test");
        
        guideService.setGuide(guide);
        
        Booking bookingRequest = new Booking();
        bookingRequest.setGuideService(guideService);
        bookingRequest.setStartDate(startDate);
        bookingRequest.setEndDate(endDate);
        
        bookingRequest.setGuide(guide);
        bookingRequest.setCustomer(customer);
        
        when(guideServiceService
        		.findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(
        				guide.getId(), 
        				guideService.getType(), 
        				guideService.getGroupSizeCategory(), 
        				guideService.getLanguage(), 
        				guideService.getCity())).thenReturn(Optional.of(guideService));
        when(bookingRepo.save(bookingRequest)).thenReturn(bookingRequest);
                
        Optional<Booking> booking = bookingService.createBooking(bookingRequest);
        
        assertNotNull(booking);
        assertEquals(3000000D, booking.get().getTotalPrice());
    }

    // TC6: Create booking for a single day
    @Test
    void TC6_createBookingForSingleDay() {
    	LocalDate startDate = LocalDate.of(2025, 5, 15);
        LocalDate endDate = LocalDate.of(2025, 5, 15);
        GuideService guideService = new GuideService();
        guideService.setLanguage(Language.English);
        guideService.setCity(City.HA_NOI);
        guideService.setPricePerDay(1000000D);
        
        User customer = new User();
        customer.setId(3L);
        customer.setFullName("User Test");
        
        User guide = new User();
        guide.setId(4L);
        guide.setFullName("Guide Test");
        
        guideService.setGuide(guide);
        
        Booking bookingRequest = new Booking();
        bookingRequest.setGuideService(guideService);
        bookingRequest.setStartDate(startDate);
        bookingRequest.setEndDate(endDate);
        
        bookingRequest.setGuide(guide);
        bookingRequest.setCustomer(customer);
        
        when(guideServiceService
        		.findByGuideIdAndTypeAndGroupSizeCategoryAndLanguageAndCity(
        				guide.getId(), 
        				guideService.getType(), 
        				guideService.getGroupSizeCategory(), 
        				guideService.getLanguage(), 
        				guideService.getCity())).thenReturn(Optional.of(guideService));
        when(bookingRepo.save(bookingRequest)).thenReturn(bookingRequest);
                
        Optional<Booking> booking = bookingService.createBooking(bookingRequest);
        
        assertNotNull(booking);
        assertEquals(1000000D, booking.get().getTotalPrice());
    }
}

