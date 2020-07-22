package org.example.auth.service.user.account;

import org.example.auth.service.user.account.dto.ChangeEmailRequest;
import org.example.auth.service.user.account.dto.ChangePasswordRequest;
import org.example.auth.service.user.account.dto.RegistrationRequest;
import org.example.auth.service.user.account.dto.UserAccountResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface UserAccountService extends UserDetailsService {

    UserAccountResponse createUser(@Valid RegistrationRequest registrationRequest);

    UserAccountResponse activateUser(@NotNull String activationCode);

    UserAccountResponse resendActivationCode(@NotNull String email);

    UserAccountResponse changePassword(@Valid ChangePasswordRequest request);

    UserAccountResponse sendResetPasswordCode(@NotNull String username);

    UserAccountResponse resetPassword(@NotNull String code);

    UserAccountResponse changeEmail(@Valid ChangeEmailRequest request);

    UserAccountResponse confirmChangeEmail(@NotNull String code);

}
