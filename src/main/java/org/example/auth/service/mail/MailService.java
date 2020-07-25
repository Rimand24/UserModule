package org.example.auth.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.Future;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name:service}")
    private String SITE_NAME;

    @Value("${mail.debug:false}")
    private boolean sendMessagesToConsole;

    private final String SITE_LINK = "http://" + serverAddress + ":" + serverPort;
    private static final String ACTIVATION_ADDRESS = "/activate";
    private static final String RESET_PASS_ADDRESS = "/resetPassword";
    private static final String EMAIL_CONFIRM_ADDRESS = "/changeEmail";

    @Async
    public Future<Boolean> sendActivationCode(String username, String email, String activationCode) {
        String message = String.format("Hello, %1$s! Welcome to our site, please follow the link bellow to finish the registration \n\r" +
                "%2$s%3$s/%4$s", username, SITE_LINK, ACTIVATION_ADDRESS, activationCode);
        String subject = "Welcome to " + SITE_NAME;

        return send(email, subject, message);
    }

    @Async
    public Future<Boolean> sendEmailRegistrationSuccess(String username, String email) {
        String message = String.format("Hello, %1$s! Registration complete", username);
        String subject = "Registration complete";
        return send(email, subject, message);
    }

    @Async
    public Future<Boolean> sendResetPasswordCode(String username, String email, String code) {
        String message = String.format("%1$s, follow the link bellow to reset password \n\r " +
                        "%2$s%3$s/%4$s \n\r " +
                        "if you dont sent reset password request, contact with support",
                username, SITE_LINK, RESET_PASS_ADDRESS, code);
        String subject = "Password reset";
        return send(email, subject, message);
    }

    @Async
    public Future<Boolean> sendNewPassword(String username, String email, String password) {
        String message = String.format("%1$s, your password had been successfully changed \n\r " +
                "Your new password: %2$s", username, password);
        String subject = "Password changed";
        return send(email, subject, message);
    }

    @Async
    public Future<Boolean> sendNewEmailConfirmCode(String username, String email, String code) {
        String message = String.format("%1$s, follow the link bellow to change email \n\r" +
                "%2$s%3$s/%4$s", username, SITE_LINK, EMAIL_CONFIRM_ADDRESS, code);
        String subject = "Email change";
        return send(email, subject, message);
    }

    @Async
    public Future<Boolean> sendNewEmailConfirmSuccess(String username, String email) {
        String message = String.format("Hello, %1$s! Your email was successfully changed", username);
        String subject = "Email successfully changed";
        return send(email, subject, message);
    }

    private Future<Boolean> send(String email, String subject, String message) {

        if (sendMessagesToConsole) {
            return sendMock(email, subject, message);
        } else {
            return sendReal(email, subject, message);
        }
    }

    private Future<Boolean> sendMock(String email, String subject, String message) {
        System.out.println("sending mail..");
        System.out.println(email);
        System.out.println(subject);
        System.out.println(message);
        try {
            Thread.sleep(1000 * 3);
        } catch (InterruptedException ignored) {
        }
        System.out.println("sending mail completed");
        return new AsyncResult<Boolean>(true);
    }

    private Future<Boolean> sendReal(String email, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage() {{
            setFrom(from);
            setTo(email);
            setSubject(subject);
            setText(message);
        }};
        mailSender.send(simpleMailMessage);
        return new AsyncResult<>(true);
    }
}