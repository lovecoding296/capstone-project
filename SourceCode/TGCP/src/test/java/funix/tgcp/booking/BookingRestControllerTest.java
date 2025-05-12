package funix.tgcp.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import funix.tgcp.config.JwtUtil;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
public class BookingRestControllerTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired 
	private JwtUtil jwtUtil;

    private static final String EXISTING_BOOKING_ID = "1";
    private static final String NON_EXISTING_BOOKING_ID = "999";
    
	@Test
	void TC01_CreateBookingSuccess() throws Exception {
		String bookingJson = """
				{
				  "customerId": 1,
				  "destination": "Hanoi",
				  "status": "PENDING",
				  "guide": { "id": 4 },
				  "startDate": "2025-06-01",
				  "endDate": "2025-06-05",
				  "guideService": {
				    "type": "TRANSLATOR",
				    "groupSizeCategory": "UNDER_5",
				    "language": "French",
				    "city": "HA_NOI",
				    "paymentOption": "FULL_PAYMENT"
				  }
				}
				""";
		String token = jwtUtil.createToken(3L, "phi@tgcp.com");

		mockMvc.perform(post("/api/bookings/create")
				.header("Authorization", "Bearer " + token)
				.contentType("application/json")
				.content(bookingJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("PENDING")).andExpect(jsonPath("$.destination").value("Hanoi"));
	}

    @Test
    void TC02_CreateBookingFail() throws Exception {
        String bookingJson = "{ \"status\": \"PENDING\" }"; // Missing destination
                
        mockMvc.perform(post("/api/bookings/create")
        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com"))
                .contentType("application/json")
                .content(bookingJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to create booking"));
    }

    @Test
    void TC03_GetBookingListByCustomer() throws Exception {
        mockMvc.perform(get("/api/bookings")
        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com"))
                .param("customer", "id: 3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customer.id").value(3));
    }

    @Test
    void TC04_GetBookingListByGuide() throws Exception {
        mockMvc.perform(get("/api/guides/bookings")
        		.header("Authorization", "Bearer " + jwtUtil.createToken(4L, "duc@tgcp.com"))
                .param("guide", "id: 3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guide.id").value(4));
    }

    @Test
    void TC05_GetBookingByIdFound() throws Exception {
        mockMvc.perform(get("/api/bookings/{id}", 2)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(4L, "duc@tgcp.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void TC06_GetBookingByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/bookings/{id}", NON_EXISTING_BOOKING_ID)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    void TC07_ConfirmBookingSuccess() throws Exception {
        mockMvc.perform(put("/api/bookings/{id}/confirm", 38)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(4L, "duc@tgcp.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking confirmed successfully"));
    }

    @Test
    void TC08_ConfirmBookingFail() throws Exception {
        mockMvc.perform(put("/api/bookings/{id}/confirm", NON_EXISTING_BOOKING_ID)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to confirm booking"));
    }

    @Test
    void TC09_CompleteBookingSuccess() throws Exception {
        mockMvc.perform(put("/api/bookings/{id}/complete", 28)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(4L, "duc@tgcp.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking completed successfully"));
    }

    //need to review because can only test once
    @Test
    void TC10_CancelBookingByUser() throws Exception {
        String reasonJson = "{ \"reason\": \"Changed plans\" }";
        
        mockMvc.perform(put("/api/bookings/{id}/cancel-by-user", 38)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com"))
                .contentType("application/json")
                .content(reasonJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking canceled successfully"));
    }

    //need to review because can only test once
    @Test
    void TC11_CancelBookingByGuide() throws Exception {
        String reasonJson = "{ \"reason\": \"Guide unavailable\" }";
        
        mockMvc.perform(put("/api/bookings/{id}/cancel-by-guide", 28)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(4L, "duc@tgcp.com"))
                .contentType("application/json")
                .content(reasonJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking canceled successfully"));
    }

    @Test
    void TC12_RejectBooking() throws Exception {
        String reasonJson = "{ \"reason\": \"Customer canceled\" }";
        
        mockMvc.perform(put("/api/bookings/{id}/reject", 38)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(4L, "duc@tgcp.com"))
                .contentType("application/json")
                .content(reasonJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking rejected successfully"));
    }

    @Test
    void TC13_DeleteBookingSuccess() throws Exception {
        mockMvc.perform(delete("/api/bookings/{id}", EXISTING_BOOKING_ID)
        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking has been deleted successfully."));
    }

    //2 hàm này không sử dụng trong service
//    @Test
//    void TC14_DeleteBookingFail() throws Exception {
//        mockMvc.perform(delete("/api/bookings/{id}", NON_EXISTING_BOOKING_ID)
//        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com")))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.message").value("Đã xảy ra lỗi khi xóa booking."));
//    }

//    @Test
//    void TC15_TestDeleteBookingViaGet() throws Exception {
//        mockMvc.perform(get("/api/bookings/delete/{id}", EXISTING_BOOKING_ID)
//        		.header("Authorization", "Bearer " + jwtUtil.createToken(3L, "phi@tgcp.com")))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Booking đã được xóa thành công."));
//    }
}