package funix.tgcp.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import funix.tgcp.exception.EmailVerificationException;
import funix.tgcp.util.EmailHelper;

@Transactional
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailHelper emailHelper;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void TC1_validEmail_triggersPasswordReset() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.forgotPassword("test@example.com");

        assertNotNull(user.getVerificationToken(), "Token should be generated");
        verify(emailHelper).sendResetPasswordEmail(eq(user.getEmail()), eq(user.getVerificationToken()));
    }

    @Test
    void TC2_nonExistingEmail_noActionTaken() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userService.forgotPassword("notfound@example.com"));

        verify(emailHelper, never()).sendResetPasswordEmail(anyString(), anyString());
    }

    @Test
    void TC3_nullEmailInput() {
        assertThrows(IllegalArgumentException.class, () -> userService.forgotPassword(null));
    }

    @Test
    void TC4_emptyEmailInput() {
        userService.forgotPassword("");
        verify(emailHelper, never()).sendResetPasswordEmail(anyString(), anyString());
    }

    @Test
    void TC5_invalidEmailFormat() {
        userService.forgotPassword("invalid-email-format");
        verify(emailHelper, never()).sendResetPasswordEmail(anyString(), anyString());
    }

    @Test
    void TC6_existingUserWithToken_replacesToken() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setVerificationToken("old-token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.forgotPassword("test@example.com");

        assertNotEquals("old-token", user.getVerificationToken(), "Token should be replaced");
        verify(emailHelper).sendResetPasswordEmail(eq(user.getEmail()), eq(user.getVerificationToken()));
    }

    @Test
    void TC7_databaseFailureDuringSave() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("DB error")).when(userRepository).save(any());

        assertThrows(RuntimeException.class, () -> userService.forgotPassword("test@example.com"));
    }
    @Test
    void TC9_invalidToken_throwsException() {
        when(userRepository.findByVerificationToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(EmailVerificationException.class, () -> userService.handleResetPassword("invalid-token", "newPass123"));
    }

    @Test
    void TC10_tokenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.handleResetPassword(null, "newPass123"));
    }

    @Test
    void TC11_passwordIsNull() {
        User user = new User();
        user.setVerificationToken("valid-token");
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.handleResetPassword("valid-token", null));
    }

    @Test
    void TC12_passwordIsEmptyString() throws EmailVerificationException {
        User user = new User();
        user.setVerificationToken("valid-token");
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("")).thenReturn("encodedEmpty");

        userService.handleResetPassword("valid-token", "");

        assertEquals("encodedEmpty", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void TC13_passwordWithSpecialChars() throws EmailVerificationException {
        User user = new User();
        user.setVerificationToken("valid-token");
        String complexPassword = "P@$$w0rd!123";
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(complexPassword)).thenReturn("encodedComplex");

        userService.handleResetPassword("valid-token", complexPassword);

        assertEquals("encodedComplex", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void TC14_saveFailsDuringUpdate() {
        User user = new User();
        user.setVerificationToken("valid-token");
        when(userRepository.findByVerificationToken("valid-token")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        doThrow(new RuntimeException("DB Error")).when(userRepository).save(user);

        assertThrows(RuntimeException.class, () -> userService.handleResetPassword("valid-token", "password"));
    }
}
