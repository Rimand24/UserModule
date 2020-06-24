package org.example.auth.service;

import org.example.auth.controller.requestDto.RegistrationForm;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }
        return user;
    }

    //
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public User findByUsername(String name) {
        return userRepo.findByUsername(name);
    }


    public void save(RegistrationForm form) {

//        User user = new User();
//        user.setUsername(form.getUsername());
//        user.setPassword(encoder.encode(form.getPassword()));
//        user.setEmail(form.getEmail());
//        Set<Role> authorities = new HashSet<>();
//        authorities.add(Role.USER);
//        user.setAuthorities(authorities);
//        user.setEnabled(true); //todo email activation
//
//        User registeredUser = userRepo.save(user);
//        response.setSuccess(true);
//        System.out.println("new user created with id "+registeredUser.getId()); //fixme

    }
}
