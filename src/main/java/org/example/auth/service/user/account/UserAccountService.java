package org.example.auth.service.user.account;

import org.example.auth.service.user.account.request.ChangeEmailRequest;
import org.example.auth.service.user.account.request.ChangePasswordRequest;
import org.example.auth.service.user.account.request.RegistrationRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface UserAccountService extends UserDetailsService {

    boolean createUser(@Valid RegistrationRequest registrationRequest);

    boolean activateUser(@NotNull String activationCode);

    boolean resendActivationCode(@NotNull String email);

    boolean changePassword(@Valid ChangePasswordRequest request);

    boolean sendResetPasswordCode(@NotNull String username);

    boolean resetPassword(@NotNull String code);

    boolean changeEmail(@Valid ChangeEmailRequest request);

    boolean confirmChangeEmail(@NotNull String code);

}
