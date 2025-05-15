package funix.tgcp.user;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String EXISTING_EMAIL = "admin@tgcp.com";
    private final String NON_EXISTING_EMAIL = "notfound@example.com";
    private final Long VALID_ID = 1L;          // Cập nhật ID đúng tùy DB
    private final Long INVALID_ID = 99999L;    // ID không tồn tại

    @Test
    void TC01_FindUserByEmail_existing() throws Exception {
        mockMvc.perform(get("/api/users/by-email")
                .param("email", EXISTING_EMAIL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(EXISTING_EMAIL));
    }

    @Test
    void TC02_FindUserByEmail_nonExisting() throws Exception {
        mockMvc.perform(get("/api/users/by-email")
                .param("email", NON_EXISTING_EMAIL))
            .andExpect(status().isNotFound());
    }

    @Test
    void TC03_GetUserProfileById_existing() throws Exception {
        mockMvc.perform(get("/api/users/{id}", VALID_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(VALID_ID));
    }

    @Test
    void TC04_GetUserProfileById_nonExisting() throws Exception {
        mockMvc.perform(get("/api/users/{id}", INVALID_ID))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("User not found")));
    }

    @Test
    void TC05_FilterUsers_noFilters() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void TC06_FilterUsers_byEmail() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("email", "test@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void TC07_FilterUsers_byFullName() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("fullName", "John"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void TC08_FilterUsers_byRole() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("role", Role.ROLE_GUIDE.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void TC09_EnableUser_validId() throws Exception {
        mockMvc.perform(put("/api/users/{id}/enable", VALID_ID))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("User enabled successfully")));
    }

//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void TC10_EnableUser_invalidId() throws Exception {
//        mockMvc.perform(put("/api/users/{id}/enable", INVALID_ID))
//            .andExpect(status().isNotFound())
//            .andExpect(jsonPath("$.message").value("User not found with id: " + INVALID_ID));
//    }

//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void TC11_DisableUser_validId() throws Exception {
//        mockMvc.perform(put("/api/users/{id}/disable", VALID_ID))
//            .andExpect(status().isOk())
//            .andExpect(content().string(containsString("User disabled successfully")));
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"ADMIN"})
//    void TC12_DisableUser_invalidId() throws Exception {
//        mockMvc.perform(put("/api/users/{id}/disable", INVALID_ID))
//            .andExpect(status().isNotFound())
//            .andExpect(jsonPath("$.message").value("User not found with id: " + INVALID_ID));
//    }
}
