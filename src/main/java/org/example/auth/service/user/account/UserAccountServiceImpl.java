package org.example.auth.service.user.account;

import lombok.extern.slf4j.Slf4j;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.user.account.dto.*;
import org.example.auth.service.util.RandomGeneratorUtils;
import org.example.auth.service.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("user login failed:(username:{}, reason: user not found)", username);
            throw new UsernameNotFoundException("user not found:" + username);
        }
        User user = optional.get();
        if (!user.isAccountNonLocked()){
            log.debug("user login failed:(username:{}, reason: user blocked)", username);
            throw new UsernameNotFoundException("user blocked:" + username);
        }
        return user;
    }

    @Override
    public UserAccountResponse createUser(@Valid RegistrationRequest registrationRequest) {
        log.debug("Registration request: (username:{}, password:{}, email:{})",
                registrationRequest.getUsername(),
                registrationRequest.getPassword(),
                registrationRequest.getEmail());

        //todo delete after tests
        if (registrationRequest == null
                || !StringUtils.hasText(registrationRequest.getUsername())
                || !StringUtils.hasText(registrationRequest.getEmail())
                || !StringUtils.hasText(registrationRequest.getPassword())) {
            throw new RuntimeException("username, email or password is invalid");
        }
        //todo end

        UserAccountResponse response = new UserAccountResponse();

        if (userRepo.findByUsername(registrationRequest.getUsername()).isPresent()) {
            response.setError(UserAccountServiceErrorCode.USERNAME_ALREADY_EXISTS);
            log.debug("Registration error: {}", UserAccountServiceErrorCode.USERNAME_ALREADY_EXISTS);
            return response;
        }

        if (userRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
            response.setError(UserAccountServiceErrorCode.EMAIL_ALREADY_EXISTS);
            log.debug("Registration error: {}", UserAccountServiceErrorCode.EMAIL_ALREADY_EXISTS);
            return response;
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

        response.setSuccess(true);
        return response;
    }

    @Override
    public UserAccountResponse activateUser(String activationCode) {
        log.debug("Activating code:{}", activationCode);
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByEmailActivationCode(activationCode);

        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND);
            log.debug("Activation error:{}", UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND);
            return response;
        }

        if (!tokenService.verifyToken(activationCode)) {
            response.setError(UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED);
            log.debug("Activation error:{}", UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED);
            return response;
        }

        User user = optional.get();
        user.setEmailActivationCode(null);
        user.setEnabled(true);
        userRepo.save(user);

        log.info("User activated:{}", user.getUsername());
        response.setSuccess(true);
        return response;
    }


    @Override
    public UserAccountResponse resendActivationCode(String email) {
        log.debug("resend activation code at email:{}", email);
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByEmail(email);

        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.EMAIL_NOT_FOUND);
            log.debug("Activation code resend error:{}", UserAccountServiceErrorCode.EMAIL_NOT_FOUND);
            return response;
        }

        User user = optional.get();

        if (user.getEmailActivationCode() == null) {
            response.setError(UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND);
            log.debug("Activation code resend error:{}", UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND);
            return response;
        }

        String activationCode = tokenService.generateEmailVerificationToken();
        user.setEmailActivationCode(activationCode);
        userRepo.save(user);
        mailService.sendActivationCode(user.getUsername(), email, activationCode);

        log.debug("Activation code resent:(email:{}, code:{})", email, activationCode);
        response.setSuccess(true);
        return response;
    }

    @Override
    public UserAccountResponse changePassword(ChangePasswordRequest request) {
        log.debug("change password username:{}", request.getUsername());
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByUsername(request.getUsername());

        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.USERNAME_NOT_FOUND);
            log.debug("Change password error:{}", UserAccountServiceErrorCode.USERNAME_NOT_FOUND);
            return response;
        }

        User user = optional.get();

        if (!user.getPassword().equals(encoder.encode(request.getOldPassword()))) {
            response.setError(UserAccountServiceErrorCode.PASSWORD_INCORRECT);
            log.debug("Change password error:{}", UserAccountServiceErrorCode.PASSWORD_INCORRECT);
            return response;
        }

        user.setPassword(encoder.encode(request.getPassword()));
        userRepo.save(user);

        log.debug("Password changed for user:{}", request.getUsername());
        response.setSuccess(true);
        return response;
    }

    @Override
    public UserAccountResponse sendResetPasswordCode(String username) {
        log.debug("sending reset password for username:{}", username);
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByUsername(username);

        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.USERNAME_NOT_FOUND);
            log.debug("Send reset password code error:{}", UserAccountServiceErrorCode.USERNAME_NOT_FOUND);
            return response;
        }

        User user = optional.get();

        String code = tokenService.generatePasswordResetToken();
        user.setPasswordResetCode(code);
        String email = user.getEmail();
        mailService.sendResetPasswordCode(username, email, code);

        log.debug("Reset password code resent:(email:{}, code:{})", email, code);
        response.setSuccess(true);
        return response;
    }

    @Override
    public UserAccountResponse resetPassword(String code) {
        log.debug("reset password for code:{}", code);
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByPasswordResetCode(code);
        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.PASSWORD_TOKEN_NOT_FOUND);
            log.debug("Reset password error:{}", UserAccountServiceErrorCode.PASSWORD_TOKEN_NOT_FOUND);
            return response;
        }

        if (!tokenService.verifyToken(code)) {
            response.setError(UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED);
            log.debug("Reset password error:{}", UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED);
            return response;
        }

        User user = optional.get();

        String newPassword = generator.generatePassword();
        user.setPassword(encoder.encode(newPassword));
        user.setPasswordResetCode(null);
        mailService.sendNewPassword(user.getUsername(), user.getEmail(), newPassword);
        userRepo.save(user);

        log.debug("Password changed for user:{}", user.getUsername());
        response.setSuccess(true);
        return response;
    }

    @Override
    public UserAccountResponse changeEmail(@Valid ChangeEmailRequest request) {
        log.debug("change email request:(username:{}, email:{})", request.getUsername(), request.getEmail());
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByUsername(request.getUsername());
        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.USERNAME_NOT_FOUND);
            log.debug("Email change error:{}", UserAccountServiceErrorCode.USERNAME_NOT_FOUND);
            return response;
        }

        User user = optional.get();

        if (!user.getPassword().equals(encoder.encode(request.getPassword()))) {
            response.setError(UserAccountServiceErrorCode.PASSWORD_INCORRECT);
            log.debug("Email change error:{}", UserAccountServiceErrorCode.PASSWORD_INCORRECT);
            return response;
        }

        if (user.getEmail().equals(request.getEmail())) {
            response.setError(UserAccountServiceErrorCode.NEW_EMAIL_IS_THE_SAME);
            log.debug("Email change error:{}", UserAccountServiceErrorCode.NEW_EMAIL_IS_THE_SAME);
            return response;
        }

        if (userRepo.findByEmail(request.getEmail()).isPresent()){
            response.setError(UserAccountServiceErrorCode.EMAIL_ALREADY_EXISTS);
            log.debug("Email change error:{}", UserAccountServiceErrorCode.EMAIL_ALREADY_EXISTS);
            return response;
        }

        String code = tokenService.generateEmailVerificationToken();
        user.setEmailActivationCode(code);
        userRepo.save(user);

        mailService.sendNewEmailConfirmCode(request.getUsername(), request.getEmail(), code);

        log.debug("Email change code resent:(email:{}, code:{})", request.getEmail(), code);
        response.setSuccess(true);
        return response;
    }

    @Override
    public UserAccountResponse confirmChangeEmail(String code) {
        log.debug("change email for code:{}", code);
        UserAccountResponse response = new UserAccountResponse();

        Optional<User> optional = userRepo.findByEmailActivationCode(code);
        if (optional.isEmpty()) {
            response.setError(UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND);
            log.debug("Email change error:{}", UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND);
            return response;
        }

        String email = tokenService.getEmailFromToken(code);
        User user = optional.get();
        user.setEmail(email);
        userRepo.save(user);

        mailService.sendNewEmailConfirmSuccess(user.getUsername(), email);

        log.debug("Email changed for user:{}", user.getUsername());
        response.setSuccess(true);
        return response;
    }
}