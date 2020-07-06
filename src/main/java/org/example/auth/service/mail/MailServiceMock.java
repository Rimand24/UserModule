package org.example.auth.service.mail;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceMock implements MailService{

    @Override
    @Async
    public void send(MailRequest request) {
        System.out.println(request.getSubject()); //todo email service
        System.out.println(request.getTo());
        System.out.println(request.getMessage()); //todo email service
    }
}
