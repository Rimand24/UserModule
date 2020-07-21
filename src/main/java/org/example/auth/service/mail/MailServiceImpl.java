package org.example.auth.service.mail;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.concurrent.Future;

//@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Override
    @Async
    public Future<Boolean> send(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage() {{
            setFrom(from);
            setTo(mail.getTo());
            setSubject(mail.getSubject());
            setText(mail.getContent());
        }};
        mailSender.send(message);
        return new AsyncResult<Boolean>(true);
    }

    //    https://memorynotfound.com/spring-mail-sending-email-thymeleaf-html-template-example/
    @SneakyThrows
    @Override
    @Async
    public Future<Boolean> sendWithAttachment(Mail mail) {
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
        return new AsyncResult<Boolean>(true);
    }


    public Future<Boolean> sendActivationCode(String username, String email, String activationCode) {


        String message = "Hello, " + username + "! Welcome to our site, please follow the link bellow to finish the registration \n\r" +
                "http://" + serverAddress + ":" + serverPort + "/activate/" + activationCode;

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


    /**
     * Welcome to Qwiklabs® John Doe!
     * <p>
     * Please click this link to confirm your account:
     * https://run.qwiklabs.com/users/confirmation?confirmation_token=oTuofqCDEgAKKu4bMbzE&locale=en
     * <p>
     * After you confirm your account, you can use your email address to log in at run.qwiklabs.com.
     * <p>
     * What next? You can find a variety of labs and quests in the lab catalog.
     * <p>
     * You can browse all our introductory labs.
     * <p>
     * If you experience any difficulty, please visit our community portal and submit a ticket at: https://qwiklab.zendesk.com.
     * <p>
     * Welcome once again - we are happy you're here!
     * <p>
     * Thank you,
     * Qwiklabs Support
     * support@qwiklabs.com
     * <p>
     * <p>
     * <p>
     * <p>
     * Активируйте свою учетную запись Zoom
     * Здравствуйте ${EMAIL},
     * Поздравляем с регистрацией Zoom!
     * Чтобы активировать вашу учетную запись, нажмите кнопку ниже для подтверждения вашего адреса электронной почты:
     * Активировать учетную запись
     * <p>
     * Если кнопка выше не работает, скопируйте в ваш браузер следующий адрес:
     * https://us04web.zoom.us/activate?code=ZOvT1EUsRfX-ifPZhOCikpFWL66IwUw3o944Q1yEogU.BQgAAAFzMA_H4gAnjQAUU3BpY3VsdW0yNEBnbWFpbC5jb20BAGQAABZlaVBUMjVIRFRwLWVNNmk3bGJuT2lBAAAAAAAAAAA&fr=signup
     * Для получения дополнительной помощи посетите наш <a href=#>Центр поддержки<a/>.
     * Успехов в использовании Zoom!
     */


    public Future<Boolean> sendResetPasswordCode(String username, String email, String code) {
        String message = username + ", follow the link bellow to reset password \n\r " +
                "http://" + serverAddress + ":" + serverPort + "/resetPassword/" + code +
                "if you dont sent reset password request, contact with support";

        sendMail(email, "Password reset", message);
        return new AsyncResult<Boolean>(true);
    }

    public Future<Boolean> sendNewPassword(String username, String email, String password) {
        String message = username + ", your password had been successfully changed \n\r " +
                "Your new password: " + password;

        sendMail(email, "Password changed", message);
        return new AsyncResult<Boolean>(true);
    }

    @Override
    public Future<Boolean> sendNewEmailConfirmCode(String username, String email, String code) {

        String message = "Hello, " + username + "! Follow the link bellow to change email \n\r" +
                "http://" + serverAddress + ":" + serverPort + "/activate/" + code;

        sendMail(email, "Email change", message);
        return new AsyncResult<Boolean>(true);

    }

    @Override
    public Future<Boolean> sendNewEmailConfirmSuccess(String username, String email) {
        String message = "Hello, " + username + "! Your email was successfully changed";

        sendMail(email, "Email successfully changed", message);
        return new AsyncResult<Boolean>(true);
    }


//
//    /////////
//    /**
//     * Populates the roles. This method is a shortcut for calling
//     * {@link #authorities(String...)}, but automatically prefixes each entry with
//     * "ROLE_". This means the following:
//     *
//     * <code>
//     *     builder.roles("USER","ADMIN");
//     * </code>
//     *
//     * is equivalent to
//     *
//     * <code>
//     *     builder.authorities("ROLE_USER","ROLE_ADMIN");
//     * </code>
//     *
//     * <p>
//     * This attribute is required, but can also be populated with
//     * {@link #authorities(String...)}.
//     * </p>
//     *
//     * @param roles the roles for this user (i.e. USER, ADMIN, etc). Cannot be null,
//     * contain null values or start with "ROLE_"
//     * @return the {@link org.springframework.security.core.userdetails.User.UserBuilder} for method chaining (i.e. to populate
//     * additional attributes for this user)
//     */
//    public org.springframework.security.core.userdetails.User.UserBuilder roles(String... roles) {
//        List<GrantedAuthority> authorities = new ArrayList<>(
//                roles.length);
//        for (String role : roles) {
//            Assert.isTrue(!role.startsWith("ROLE_"), () -> role
//                    + " cannot start with ROLE_ (it is automatically added)");
//            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
//        }
//        return authorities(authorities);
//    }
//    ///////////////////////////


}