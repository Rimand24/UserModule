package org.example.auth.service.user.account;

import lombok.extern.slf4j.Slf4j;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.user.account.request.ChangeEmailRequest;
import org.example.auth.service.user.account.request.ChangePasswordRequest;
import org.example.auth.service.user.account.request.RegistrationRequest;
import org.example.auth.service.util.RandomGeneratorUtils;
import org.example.auth.service.util.TokenService;
import org.example.auth.service.util.TokenServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    RandomGeneratorUtils generator;

    //UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user " + username + " not found"));
    }

    @Override
    public boolean createUser(@Valid RegistrationRequest registrationRequest) {
        //todo delete after tests
//        if (registrationRequest == null
//                || !StringUtils.hasText(registrationRequest.getUsername())
//                || !StringUtils.hasText(registrationRequest.getEmail())
//                || !StringUtils.hasText(registrationRequest.getPassword())) {
//            throw new UserRegistrationException("username, email or password is invalid");
//        }

        if (userRepo.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new UserAccountServiceException("user " + registrationRequest.getUsername() + " already exists");
        }

        if (userRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new UserAccountServiceException("user with email " + registrationRequest.getEmail() + " already exists");
        }

        String username = registrationRequest.getUsername();
        String email = registrationRequest.getEmail();
        String activationCode = tokenService.generateEmailVerificationToken();

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(registrationRequest.getPassword()));
        user.setEmail(email);
        user.setAuthorities(Collections.singleton(Role.USER));
        user.setEmailActivationCode(activationCode);
        user.setRegistrationDate(LocalDate.now());
        userRepo.save(user);
        log.info("User saved: (username:{},email:{})", user.getUsername(), user.getEmail());

        mailService.sendActivationCode(username, email, activationCode);
        return true;
    }

    @Override
    public boolean activateUser(String activationCode) {
        Optional<User> optional = userRepo.findByEmailActivationCode(activationCode);
        if (optional.isPresent()) {
            if (tokenService.verifyToken(activationCode)) { //todo handdle exception
                User user = optional.get();
                user.setEmailActivationCode(null);
                user.setEnabled(true);
                userRepo.save(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean resendActivationCode(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserAccountServiceException("user with email " + email + " not found"));

        if (user.getEmailActivationCode() == null) {
            throw new UserAccountServiceException("user " + user.getUsername() + " already activated");
        }

        String activationCode = tokenService.generateEmailVerificationToken();
        user.setEmailActivationCode(activationCode);
        userRepo.save(user);
        mailService.sendActivationCode(user.getUsername(), email, activationCode);
        return true;
    }

    @Override
    public boolean changePassword(ChangePasswordRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserAccountServiceException("user " + request.getUsername() + " not found"));

        if (!user.getPassword().equals(encoder.encode(request.getOldPassword()))) {
            throw new UserAccountServiceException("old password incorrect");
        }

        user.setPassword(encoder.encode(request.getPassword()));
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean sendResetPasswordCode(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserAccountServiceException("user: " + username + " not found"));

        String code = tokenService.generatePasswordResetToken();
        user.setPasswordResetCode(code);
        String email = user.getEmail();
        mailService.sendResetPasswordCode(username, email, code);
        return false;
    }

    @Override
    public boolean resetPassword(String code) {
        User user = userRepo.findByPasswordResetCode(code)
                .orElseThrow(() -> new UserAccountServiceException("reset code (" + code + ") not found"));

        try {
            tokenService.verifyToken(code);
            String newPassword = generator.generatePassword();
            user.setPassword(encoder.encode(newPassword));
            user.setPasswordResetCode(null);
            mailService.sendNewPassword(user.getUsername(), user.getEmail(), newPassword);
            userRepo.save(user);
            return true;
        } catch (TokenServiceException e) {
            return false;
        }

    }

    @Override
    public boolean changeEmail(@Valid ChangeEmailRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserAccountServiceException("user: " + request.getUsername() + " not found"));

        if (!user.getPassword().equals(encoder.encode(request.getPassword()))) {
            throw new UserAccountServiceException("password incorrect");
        }

        if (user.getEmail().equals(request.getEmail())) {
            throw new UserAccountServiceException("old email match with new one");
        }

        String code = tokenService.generateEmailVerificationToken();
        user.setEmailActivationCode(code);
        userRepo.save(user);

        mailService.sendNewEmailConfirmCode(request.getUsername(), request.getEmail(), code);

        return true;
    }

    @Override
    public boolean confirmChangeEmail(String code) {
        User user = userRepo.findByEmailActivationCode(code)
                .orElseThrow(() -> new UserAccountServiceException("code (" + code + ") not found"));

        String email = tokenService.getEmailFromToken(code);

        user.setEmail(email);
        userRepo.save(user);

        mailService.sendNewEmailConfirmSuccess(user.getUsername(), email);
        return true;
    }
}