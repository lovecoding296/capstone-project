package funix.tgcp.util;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailHelper {
	private final JavaMailSender mailSender;

    public EmailHelper(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Async
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
    
    
    @Async
    public void sendForgotPasswordEmail(String toEmail, String token) {
        // Tạo link reset password
        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        // Tạo nội dung email
        String subject = "Password Reset Request";
        String message = "Dear user,\n\n"
                       + "We received a request to reset your password. "
                       + "Please click the link below to reset it:\n\n"
                       + resetLink + "\n\n"
                       + "If you did not request a password reset, please ignore this email.\n\n"
                       + "Thanks,\n"
                       + "Your App Team";

        // Tạo mail đơn giản
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        // Gửi email
        mailSender.send(email);
    }
    
    @Async
    public void sendApprovalNotificationEmail(String toEmail) {
        System.out.println("sendApprovalNotificationEmail");
        
        String subject = "Account Approved";
        String text = "Congratulations! Your account has been approved by the admin. "
                    + "You can now log in and use the services on our website.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phibh96@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        
        System.out.println(message.toString());

        // Send the email
        try {
            mailSender.send(message);
            System.out.println("Approval notification email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending approval notification email: " + e);
        }
    }
    
    
    @Async
    public void sendKycRejectEmail(String toEmail, String rejectReason) {
        System.out.println("sendKycRejectEmail");

        String subject = "KYC Rejection Notification";
        String text = "Dear User,\n\n"
                    + "We regret to inform you that your KYC (Know Your Customer) request has been rejected. "
                    + "Reason for rejection: " + rejectReason + "\n\n"
                    + "If you have any questions or need further assistance, please contact our support team.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("phibh96@gmail.com");  // Địa chỉ email của bạn
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        System.out.println(message.toString());

        // Send the email
        try {
            mailSender.send(message);
            System.out.println("KYC rejection email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.out.println("Error sending KYC rejection email: " + e);
        }
    }


}
