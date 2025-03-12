package funix.tca.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import funix.tca.entity.AppUser;
import funix.tca.service.AppUserService;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AppUserService appUserService;

    public CustomAuthenticationSuccessHandler(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Fetch user from database
        Optional<AppUser> user = appUserService.findByEmail(username);
        
        // Save user in session
        HttpSession session = request.getSession();
        session.setAttribute("loggedInUser", user.get());
        
        // Redirect to profile page
        response.sendRedirect("/");
    }
}
