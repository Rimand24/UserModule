package org.example.auth.service.registration;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.mail.mailRequest;
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
        String activationCode = tokenService.generateEmailVerificationToken(username);

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

        mailRequest request = new mailRequest() {{
            setAddressee(email);
            setSubject("Activation code");
            setMessage(message);
        }};

        mailService.send(request);
    }

}