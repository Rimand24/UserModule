package org.example.auth.service.user.account;

import lombok.extern.slf4j.Slf4j;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.user.account.dto.ChangeEmailRequest;
import org.example.auth.service.user.account.dto.ChangePasswordRequest;
import org.example.auth.service.user.account.dto.RegistrationRequest;
import org.example.auth.service.user.account.dto.UserAccountResponse;
import org.example.auth.service.util.RandomGeneratorUtils;
import org.example.auth.service.util.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.example.auth.service.user.account.dto.UserAccountServiceResponseCode.*;

@Slf4j
@Service
public class UserAccountService implements UserDetailsService {

    private final UserRepo userRepo;
    private final MailService mailService;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;
    private final RandomGeneratorUtils generator;

    @Autowired
    public UserAccountService(UserRepo userRepo, MailService mailService, PasswordEncoder encoder, TokenService tokenService, RandomGeneratorUtils generator) {
        this.userRepo = userRepo;
        this.mailService = mailService;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.generator = generator;
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("user login failed:(username:{}, reason: user not found)", username);
            throw new UsernameNotFoundException("user not found:" + username);
        }
        User user = optional.get();
        if (!user.isAccountNonLocked()) {
            log.debug("user login failed:(username:{}, reason: user blocked)", username);
            throw new UsernameNotFoundException("user blocked:" + username);
        }
        return user;
    }

    public UserAccountResponse createUser(@Valid RegistrationRequest registrationRequest) {
        log.debug("Registration request: (username:{}, password:{}, email:{})",
                registrationRequest.getUsername(),
                registrationRequest.getPassword(),
                registrationRequest.getEmail());

        if (userRepo.findByUsername(registrationRequest.getUsername()).isPresent()) {
            log.debug("Registration error: {}", USERNAME_ALREADY_EXISTS);
            return new UserAccountResponse(USERNAME_ALREADY_EXISTS);
        }

        if (userRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
            log.debug("Registration error: {}", EMAIL_ALREADY_EXISTS);
            return new UserAccountResponse(EMAIL_ALREADY_EXISTS);
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

        mailService.sendActivationCode(username, email, activationCode);

        log.info("User saved: (username:{},email:{})", user.getUsername(), user.getEmail());
        return new UserAccountResponse(OK);

    }

    public UserAccountResponse activateUser(@NotNull String activationCode) {
        log.debug("Activating code:{}", activationCode);

        Optional<User> optional = userRepo.findByEmailActivationCode(activationCode);

        if (optional.isEmpty()) {
            log.debug("Activation error:{}", EMAIL_TOKEN_NOT_FOUND);
            return new UserAccountResponse(EMAIL_TOKEN_NOT_FOUND);
        }

        if (!tokenService.verifyToken(activationCode)) {
            log.debug("Activation error:{}", TOKEN_NOT_VERIFIED);
            return new UserAccountResponse(TOKEN_NOT_VERIFIED);
        }

        User user = optional.get();
        user.setEmailActivationCode(null);
        user.setEnabled(true);
        userRepo.save(user);

        mailService.sendEmailRegistrationSuccess(user.getUsername(), user.getEmail());

        log.info("User activated:{}", user.getUsername());
        return new UserAccountResponse(OK);
    }

    public UserAccountResponse resendActivationCode(@NotNull String email) {
        log.debug("resend activation code at email:{}", email);

        Optional<User> optional = userRepo.findByEmail(email);

        if (optional.isEmpty()) {
            log.debug("Activation code resend error:{}", EMAIL_NOT_FOUND);
            return new UserAccountResponse(EMAIL_NOT_FOUND);
        }

        User user = optional.get();

        //fixme posible logic error/ send code or generate new?
        if (user.getEmailActivationCode() == null) {
            log.debug("Activation code resend error:{}", ALREADY_ACTIVATED);
            return new UserAccountResponse(ALREADY_ACTIVATED);
        }

        String activationCode = tokenService.generateEmailVerificationToken();
        user.setEmailActivationCode(activationCode);
        userRepo.save(user);
        mailService.sendActivationCode(user.getUsername(), email, activationCode);

        log.debug("Activation code resent:(email:{}, code:{})", email, activationCode);
        return new UserAccountResponse(OK);
    }

    public UserAccountResponse changePassword(@Valid ChangePasswordRequest request) {
        log.debug("change password username:{}", request.getUsername());
        Optional<User> optional = userRepo.findByUsername(request.getUsername());
        if (optional.isEmpty()) {
            log.debug("Change password error:{}", USERNAME_NOT_FOUND);
            return new UserAccountResponse(USERNAME_NOT_FOUND);
        }
        User user = optional.get();
        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            log.debug("Change password error:{}", PASSWORD_INCORRECT);
            return new UserAccountResponse(PASSWORD_INCORRECT);
        }
        user.setPassword(encoder.encode(request.getPassword()));
        userRepo.save(user);
        log.debug("Password changed for user:{}", request.getUsername());
        return new UserAccountResponse(OK);
    }

    public UserAccountResponse sendResetPasswordCode(@NotNull String username) {
        log.debug("sending reset password for username:{}", username);
        Optional<User> optional = userRepo.findByUsername(username);
        if (optional.isEmpty()) {
            log.debug("Send reset password code error:{}", USERNAME_NOT_FOUND);
            return new UserAccountResponse(USERNAME_NOT_FOUND);
        }
        User user = optional.get();
        String code = tokenService.generatePasswordResetToken();
        user.setPasswordResetCode(code);
        String email = user.getEmail();
        mailService.sendResetPasswordCode(username, email, code);
        log.debug("Reset password code resent:(email:{}, code:{})", email, code);
        return new UserAccountResponse(OK);
    }

    public UserAccountResponse resetPassword(@NotNull String code) {
        log.debug("reset password for code:{}", code);
        Optional<User> optional = userRepo.findByPasswordResetCode(code);
        if (optional.isEmpty()) {
            log.debug("Reset password error:{}", PASSWORD_TOKEN_NOT_FOUND);
            return new UserAccountResponse(PASSWORD_TOKEN_NOT_FOUND);
        }
        if (!tokenService.verifyToken(code)) {
            log.debug("Reset password error:{}", TOKEN_NOT_VERIFIED);
            return new UserAccountResponse(TOKEN_NOT_VERIFIED);
        }
        User user = optional.get();
        String newPassword = generator.generatePassword();
        user.setPassword(encoder.encode(newPassword));
        user.setPasswordResetCode(null);
        mailService.sendNewPassword(user.getUsername(), user.getEmail(), newPassword);
        userRepo.save(user);
        log.debug("Password changed for user:{}", user.getUsername());
        return new UserAccountResponse(OK);
    }

    public UserAccountResponse changeEmail(@Valid ChangeEmailRequest request) {
        log.debug("change email request:(username:{}, email:{})", request.getUsername(), request.getEmail());
        Optional<User> optional = userRepo.findByUsername(request.getUsername());
        if (optional.isEmpty()) {
            log.debug("Email change error:{}", USERNAME_NOT_FOUND);
            return new UserAccountResponse(USERNAME_NOT_FOUND);
        }
        User user = optional.get();
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            log.debug("Email change error:{}", PASSWORD_INCORRECT);
            return new UserAccountResponse(PASSWORD_INCORRECT);
        }
        if (user.getEmail().equals(request.getEmail())) {
            log.debug("Email change error:{}", NEW_EMAIL_IS_THE_SAME);
            return new UserAccountResponse(NEW_EMAIL_IS_THE_SAME);
        }
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            log.debug("Email change error:{}", EMAIL_ALREADY_EXISTS);
            return new UserAccountResponse(EMAIL_ALREADY_EXISTS);
        }
        String code = tokenService.generateEmailVerificationToken();
        user.setEmailActivationCode(code);
        userRepo.save(user);
        mailService.sendNewEmailConfirmCode(request.getUsername(), request.getEmail(), code);
        log.debug("Email change code resent:(email:{}, code:{})", request.getEmail(), code);
        return new UserAccountResponse(OK);
    }

    public UserAccountResponse confirmChangeEmail(@NotNull String code) {
        log.debug("change email for code:{}", code);
        Optional<User> optional = userRepo.findByEmailActivationCode(code);
        if (optional.isEmpty()) {
            log.debug("Email change error:{}", EMAIL_TOKEN_NOT_FOUND);
            return new UserAccountResponse(EMAIL_TOKEN_NOT_FOUND);
        }
        String email = tokenService.getEmailFromToken(code);
        User user = optional.get();
        user.setEmail(email);
        userRepo.save(user);
        mailService.sendNewEmailConfirmSuccess(user.getUsername(), email);
        log.debug("Email changed for user:{}", user.getUsername());
        return new UserAccountResponse(OK);
    }
}