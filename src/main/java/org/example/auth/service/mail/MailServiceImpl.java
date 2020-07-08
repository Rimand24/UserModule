package org.example.auth.service.mail;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.internet.MimeMessage;
import java.io.File;

//@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    @Async
    public void send(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage() {{
            setFrom(from);
            setTo(mail.getTo());
            setSubject(mail.getSubject());
            setText(mail.getContent());
        }};
        mailSender.send(message);
    }

//    https://memorynotfound.com/spring-mail-sending-email-thymeleaf-html-template-example/
    @SneakyThrows
    public void sendWithAttachment(Mail mail) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setSubject(mail.getSubject());
        helper.setText(mail.getContent());
        helper.setTo(mail.getTo());
        helper.setFrom(from);

        if (mail.getFiles().isEmpty()) {
            for (File file : mail.getFiles()) helper.addAttachment(file.getName(), file);
        }
        mailSender.send(message);
    }
}