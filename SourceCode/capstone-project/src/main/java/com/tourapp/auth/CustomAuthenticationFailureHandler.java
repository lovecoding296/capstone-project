package com.tourapp.auth;

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
		String errorMessage = "Sai tên đăng nhập hoặc mật khẩu!."; // Lỗi mặc định

		if (exception instanceof BadCredentialsException) {
			errorMessage = "Sai tên đăng nhập hoặc mật khẩu.";
		} else if (exception instanceof DisabledException) {
			errorMessage = "Tài khoản của bạn chưa được xác minh. Vui lòng kiểm tra email của bạn.";

		} else if (exception instanceof LockedException) {
			errorMessage = "Tài khoản của bạn đang chờ quản trị viên phê duyệt."; // Chỉ GUIDE,AGENCY mới bị chặn KYC
		}

		response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, "UTF-8"));
	}
}
