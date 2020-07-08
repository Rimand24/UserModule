package org.example.auth.service.registration;

import javax.validation.constraints.NotNull;

public interface UserRegistrationService {

    boolean createUser(RegistrationRequest registrationRequest);

    boolean activateUser(@NotNull String activationCode);

    boolean resendActivationCode(String email);
}
