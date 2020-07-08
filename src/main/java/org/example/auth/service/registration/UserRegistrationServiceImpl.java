package org.example.auth.service.registration;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.mail.MailRequest;
import org.example.auth.service.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    private void sendActivationCode(String username, String email, String activationCode) {
        String message = "Hello, " + username + "! Welcome to our site, please follow the link bellow to finish the registration \n\r" +
                "http://" + serverAddress + serverPort + "/activate/" + activationCode;

        MailRequest request = new MailRequest() {{
            setTo(email);
            setSubject("Welcome"); //Welcome to Qwiklabs®
            setMessage(message);
        }};

        mailService.send(request);
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
     */
}