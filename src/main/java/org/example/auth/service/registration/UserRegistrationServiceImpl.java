package org.example.auth.service.registration;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.mail.Mail;
import org.example.auth.service.user.ChangePasswordRequest;
import org.example.auth.service.user.UserServiceException;
import org.example.auth.service.util.RandomStringGenerator;
import org.example.auth.service.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    RandomStringGenerator generator;

    @Value("server.address")
    private String serverAddress;

    @Value("server.port")
    private String serverPort;

    @Override
    public boolean createUser(RegistrationRequest registrationRequest) {

        //todo request validation
        if (registrationRequest == null
                || !StringUtils.hasText(registrationRequest.getUsername())
                || !StringUtils.hasText(registrationRequest.getEmail())
                || !StringUtils.hasText(registrationRequest.getPassword())) {
            throw new UserRegistrationException("username, email or password is invalid");
        }

        User userByUsername = userRepo.findByUsername(registrationRequest.getUsername());
        if (userByUsername != null) {
            throw new UserRegistrationException("user already exists");
        }

        User userByEmail = userRepo.findByEmail(registrationRequest.getEmail());
        if (userByEmail != null) {
            throw new UserRegistrationException("user with such email already exists");
        }

        String username = registrationRequest.getUsername();
        String email = registrationRequest.getEmail();
        String activationCode = tokenService.generateEmailVerificationToken();

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(registrationRequest.getPassword()));
        user.setEmail(email);
        user.setAuthorities(new HashSet<>() {{
            add(Role.USER);
        }});
        user.setActivationCode(activationCode);
        user.setRegistrationDate(LocalDate.now());
        userRepo.save(user);

        sendActivationCode(username, email, activationCode);
        return true;
    }

    @Override
    public boolean activateUser(String activationCode) { //todo not null

        User user = userRepo.findByActivationCode(activationCode);
        if (user != null) {
            if (tokenService.verifyToken(activationCode)) {
                user.setActivationCode(null);
                user.setEnabled(true);
                userRepo.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean resendActivationCode(String email) {

        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserRegistrationException("user with email " + email + " not found");
        }

        if (user.getActivationCode() == null) {
            throw new UserRegistrationException("already activated");
        }

        String activationCode = tokenService.generateEmailVerificationToken();
        user.setActivationCode(activationCode);
        userRepo.save(user);
        sendActivationCode(user.getUsername(), email, activationCode);
        return true;
    }

    @Override
    public boolean changePassword(ChangePasswordRequest request) {
        User user = userRepo.findByUsername(request.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("user " + request.getUsername() + " not found");
        }
        if (!user.getPassword().equals(encoder.encode(request.getOldPassword()))) {
            throw new UserServiceException("old password incorrect");
        }

        user.setPassword(encoder.encode(request.getPassword()));
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean sendResetPasswordCode(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }
        String code = tokenService.generatePasswordResetToken();
        user.setPasswordResetCode(code);
        String email = user.getEmail();
        sendResetPasswordCode(username, email, code);
        return false;
    }

    @Override
    public boolean resetPassword(String code) {
        User user = userRepo.findByPasswordResetCode(code);
        if (user != null) {
            if (tokenService.verifyToken(code)) {

                String newPassword = generator.generatePassword();
                user.setPassword(encoder.encode(newPassword));
                user.setPasswordResetCode(null);
                sendNewPassword(user.getUsername(), user.getEmail(), newPassword);
                userRepo.save(user);
                return true;
            }
        }
        return false;
    }


    private void sendActivationCode(String username, String email, String activationCode) {
        String message = "Hello, " + username + "! Welcome to our site, please follow the link bellow to finish the registration \n\r" +
                "http://" + serverAddress + serverPort + "/activate/" + activationCode;

        sendMail(email, "Welcome", message);
    }

    private void sendResetPasswordCode(String username, String email, String code) {
        String message = username+", follow the link bellow to reset password \n\r " +
                "http://" + serverAddress + serverPort + "/resetPassword/" + code +
                "if you dont sent reset password request, contact with support";

        sendMail(email, "Password reset", message);
    }

    private void sendNewPassword(String username, String email, String password) {
        String message = username+", your password had been successfully changed \n\r " +
                "Your new password: " + password;

        sendMail(email, "Password changed", message);
    }

    private void sendMail(String email, String subject, String message) {
        Mail mail = new Mail() {{
            setTo(email);
            setSubject("Welcome"); //Welcome to Qwiklabs®
            setContent(message);
        }};

        mailService.send(mail);
    }

    /**
     * Welcome to Qwiklabs® John Doe!
     *
     * Please click this link to confirm your account:
     * https://run.qwiklabs.com/users/confirmation?confirmation_token=oTuofqCDEgAKKu4bMbzE&locale=en
     *
     * After you confirm your account, you can use your email address to log in at run.qwiklabs.com.
     *
     * What next? You can find a variety of labs and quests in the lab catalog.
     *
     * You can browse all our introductory labs.
     *
     * If you experience any difficulty, please visit our community portal and submit a ticket at: https://qwiklab.zendesk.com.
     *
     * Welcome once again - we are happy you're here!
     *
     * Thank you,
     * Qwiklabs Support
     * support@qwiklabs.com
     *
     *
     *
     *
     * Активируйте свою учетную запись Zoom
     *Здравствуйте ${EMAIL},
     * Поздравляем с регистрацией Zoom!
     * Чтобы активировать вашу учетную запись, нажмите кнопку ниже для подтверждения вашего адреса электронной почты:
     * Активировать учетную запись
     *
     * Если кнопка выше не работает, скопируйте в ваш браузер следующий адрес:
     * https://us04web.zoom.us/activate?code=ZOvT1EUsRfX-ifPZhOCikpFWL66IwUw3o944Q1yEogU.BQgAAAFzMA_H4gAnjQAUU3BpY3VsdW0yNEBnbWFpbC5jb20BAGQAABZlaVBUMjVIRFRwLWVNNmk3bGJuT2lBAAAAAAAAAAA&fr=signup
     * Для получения дополнительной помощи посетите наш <a href=#>Центр поддержки<a/>.
     * Успехов в использовании Zoom!
     */
}