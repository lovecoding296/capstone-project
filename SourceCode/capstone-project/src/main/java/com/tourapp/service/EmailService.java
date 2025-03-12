package com.tourapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    
    public void sendVerificationEmail(String toEmail, String token) {
    	logger.info("sendVerificationEmail");
        String subject = "Email Verification";
        String text = "Please verify your email by clicking the following link: "
                + "http://localhost:8080/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phibh96@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        
        logger.info(message.toString());

        // Send the email
        try {
            mailSender.send(message);
            logger.info("Verification email sent successfully to: " + toEmail);
        } catch (Exception e) {
            logger.error("Error sending verification email", e);
        }
    }
}
