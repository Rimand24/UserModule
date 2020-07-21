package org.example.auth.service.mail;

import java.util.concurrent.Future;

public interface MailService {
    Future<Boolean> send(Mail mail);
    Future<Boolean> sendWithAttachment(Mail mail);

    Future<Boolean> sendActivationCode(String username, String email, String activationCode);

    Future<Boolean> sendResetPasswordCode(String username, String email, String code);

    Future<Boolean> sendNewPassword(String username, String email, String newPassword);

    Future<Boolean> sendNewEmailConfirmCode(String username, String email, String code);

    Future<Boolean> sendNewEmailConfirmSuccess(String username, String email);
}
