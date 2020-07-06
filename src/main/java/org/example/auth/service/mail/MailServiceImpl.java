package org.example.auth.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

//@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Override
    @Async
    public void send(MailRequest request) {
        SimpleMailMessage mailMessage = new SimpleMailMessage() {{
            setFrom(username);
            setTo(request.getTo());
            setSubject(request.getSubject());
            setText(request.getMessage());
        }};
        mailSender.send(mailMessage);
    }
}