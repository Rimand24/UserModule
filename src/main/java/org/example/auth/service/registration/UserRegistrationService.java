package org.example.auth.service.registration;

import org.example.auth.service.registration.dto.RegistrationRequest;
import org.example.auth.service.registration.dto.RegistrationResponse;

public interface UserRegistrationService {

    RegistrationResponse createUser(RegistrationRequest registrationRequest);
}
