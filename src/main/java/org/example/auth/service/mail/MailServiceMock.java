package org.example.auth.service.mail;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class MailServiceMock implements MailService {

    @Override
    @Async
    @SneakyThrows
    public Future<Boolean> send(Mail mail) {
        System.out.println("sending mail..");
        System.out.println(mail.getSubject());
        System.out.println(mail.getTo());
        System.out.println(mail.getContent());
        Thread.sleep(1000 * 10);
        System.out.println("sending mail completed");
        return new AsyncResult<Boolean>(true);
    }

    @Override
    @Async
    @SneakyThrows
    public Future<Boolean> sendWithAttachment(Mail mail) {
        System.out.println("sending mail..");
        System.out.println(mail.getSubject());
        System.out.println(mail.getTo());
        System.out.println(mail.getContent());
        mail.getFiles().forEach(file -> System.out.println(file.getName()));
        Thread.sleep(1000 * 10);
        System.out.println("sending mail completed");
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendActivationCode(String username, String email, String activationCode) {

        String message = "Hello, " + username + "! Welcome to our site, please follow the link bellow to finish the registration \n\r" +
                "http://" + "localhost" + ":" + "8080" + "/activate/" + activationCode;

        sendMail(email, "Welcome", message);
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendResetPasswordCode(String username, String email, String code) {
        System.out.println("not worked sendResetPasswordCode");
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendNewPassword(String username, String email, String newPassword) {
        System.out.println("not worked sendNewPassword");
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendNewEmailConfirmCode(String username, String email, String code) {
        System.out.println("not worked sendNewEmailConfirmCode");
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendNewEmailConfirmSuccess(String username, String email) {
        String message = "email changed";

        sendMail(email, "Welcome", message);
        return new AsyncResult<Boolean>(true);
    }

    @SneakyThrows
    private void sendMail(String email, String subject, String message) {
        Mail mail = new Mail() {{
            setTo(email);
            setSubject("Welcome"); //Welcome to Qwiklabs®
            setContent(message);
        }};

        Future future = send(mail);
        Boolean result = (Boolean) future.get();
        //log result

    }
}
