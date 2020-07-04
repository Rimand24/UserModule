package org.example.auth.service.registration;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.util.TokenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @Override
    public boolean createUser(RegistrationRequest registrationRequest) {

        //todo request validation
        if (registrationRequest == null
                || !StringUtils.hasText(registrationRequest.getUsername())
                || !StringUtils.hasText(registrationRequest.getEmail())
                || !StringUtils.hasText(registrationRequest.getPassword())) {
            throw new RuntimeException("username, email or password is invalid");
        }

        User userByUsername = userRepo.findByUsername(registrationRequest.getUsername());
        if (userByUsername != null) {
            throw new RuntimeException("user already exists");
        }

        User userByEmail = userRepo.findByEmail(registrationRequest.getEmail());
        if (userByEmail != null) {
            throw new RuntimeException("user with such email already exists");
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(encoder.encode(registrationRequest.getPassword()));
        user.setEmail(registrationRequest.getEmail());

    /*    Set<Role> authorities = new HashSet<>() {{
            add(Role.USER);
        }};*/
        //authorities.add(Role.USER);
        //String activationCode = UUID.randomUUID().toString(); //todo
        user.setAuthorities(new HashSet<>() {{add(Role.USER);}});

        String activationCode = tokenService.generateEmailVerificationToken(registrationRequest.getUsername());
        sendActivationCode(activationCode);
        user.setActivationCode(activationCode);

        userRepo.save(user);
        return true;
    }

    @Override
    public boolean activateUser(String activationCode) { //todo not null

        User user = userRepo.findByActivationCode(activationCode);
        if (user != null) {
            //boolean verified = tokenService.verifyToken(activationCode);
            if (tokenService.verifyToken(activationCode)) {
                user.setActivationCode(null);
                user.setEnabled(true);
                userRepo.save(user);
                return true;
            }
        }
        return false;
    }

    private void sendActivationCode(String activationCode) {
        System.out.println(activationCode); //todo email service
    }

}