package org.example.auth.service.mail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceMock implements MailService{

    @Override
    @Async
    public void send(Mail mail) {
        System.out.println(mail.getSubject());
        System.out.println(mail.getTo());
        System.out.println(mail.getContent());
    }

    @Override
    public void sendWithAttachment(Mail mail) {
        System.out.println(mail.getSubject());
        System.out.println(mail.getTo());
        System.out.println(mail.getContent());
        mail.getFiles().forEach(file -> System.out.println(file.getName()));
    }
}
