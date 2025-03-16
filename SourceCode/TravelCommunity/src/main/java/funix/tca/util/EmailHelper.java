package funix.tca.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailHelper {
	private final JavaMailSender mailSender;

    public EmailHelper(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendVerificationEmail(String toEmail, String token) {
    	System.out.println("sendVerificationEmail");
        String subject = "Email Verification";
        String text = "Please verify your email by clicking the following link: "
                + "http://localhost:8080/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phibh96@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        
        System.out.println(message.toString());

        // Send the email
        try {
            mailSender.send(message);
            System.out.println("Verification email sent successfully to: " + toEmail);
        } catch (Exception e) {
        	System.out.println("Error sending verification email " + e);
        }
    }
}
