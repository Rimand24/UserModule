package org.example.auth.service.registration;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public RegistrationResponse register(RegistrationRequest registrationRequest) {
        RegistrationResponse response = new RegistrationResponse();

        if (registrationRequest.getUsername().isBlank() || registrationRequest.getEmail().isBlank() || registrationRequest.getPassword().isBlank()) {
            response.addError("username, email or password is invalid");
            return response;
        }

        User userByUsername = userRepo.findByUsername(registrationRequest.getUsername());
        if (userByUsername != null) {
            response.addError("user already exists");
        }

        User userByEmail = userRepo.findByEmail(registrationRequest.getEmail());
        if (userByEmail != null) {
            response.addError("user with such email already exists");
        }

        User user = modelMapper.map(registrationRequest, User.class);
        Set<Role> authorities = new HashSet<>();
        authorities.add(Role.USER);
        user.setAuthorities(authorities);

        User registeredUser = userRepo.save(user);
        if (registeredUser.getId() > 0) {
            response.setSuccess(true);
        }

        return response;
    }
}