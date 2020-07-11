package org.example.auth.service.registration;

import org.example.auth.service.user.ChangePasswordRequest;

import javax.validation.constraints.NotNull;

public interface UserRegistrationService {

    boolean createUser(RegistrationRequest registrationRequest);

    boolean activateUser(@NotNull String activationCode);

    boolean resendActivationCode(String email);

    boolean changePassword(ChangePasswordRequest request);

    boolean sendResetPasswordCode(String username);

    boolean resetPassword(String code);

}
