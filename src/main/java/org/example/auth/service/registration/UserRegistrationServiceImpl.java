package org.example.auth.service.registration;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public RegistrationResponse createUser(RegistrationRequest registrationRequest) {
        RegistrationResponse response = new RegistrationResponse();


        if (registrationRequest == null
                || !StringUtils.hasText(registrationRequest.getUsername())
                || !StringUtils.hasText(registrationRequest.getEmail())
                || !StringUtils.hasText(registrationRequest.getPassword())) {
            response.addError("username, email or password is invalid");
            return response;
        }

        User userByUsername = userRepo.findByUsername(registrationRequest.getUsername());
        if (userByUsername != null) {
            response.addError("user already exists");
            return response;
        }

        User userByEmail = userRepo.findByEmail(registrationRequest.getEmail());
        if (userByEmail != null) {
            response.addError("user with such email already exists");
            return response;
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(encoder.encode(registrationRequest.getPassword()));
        user.setEmail(registrationRequest.getEmail());
        Set<Role> authorities = new HashSet<>();
        authorities.add(Role.USER);
        user.setAuthorities(authorities);
        user.setEnabled(true); //todo email activation

        User registeredUser = userRepo.save(user);
        response.setSuccess(true);
        System.out.println("new user created with id "+registeredUser.getId()); //fixme

        return response;
    }
}