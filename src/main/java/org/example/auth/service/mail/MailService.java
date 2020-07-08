package org.example.auth.service.mail;

public interface MailService {
    void send(Mail mail);
    void sendWithAttachment(Mail mail);
}

