package funix.tgcp.config;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String errorMessage = "Incorrect username or password."; // Lỗi mặc định

		if (exception instanceof BadCredentialsException) {
			errorMessage = "Incorrect username or password.";
		} else if (exception instanceof DisabledException) {
			errorMessage = "Your account has been locked.";

		}

		response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
	}
}
