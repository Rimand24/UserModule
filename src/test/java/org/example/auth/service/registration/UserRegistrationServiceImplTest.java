package org.example.auth.service.registration;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.util.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRegistrationServiceImplTest {

    @InjectMocks
    UserRegistrationServiceImpl registrationService;

    @Mock
    UserRepo userRepo;

    @Mock
    TokenService tokenService;

    @Mock
    PasswordEncoder passwordEncoder;

    private final String username = "Alex";
    private final String email = "example@mail.com";
    private final String password = "1234";
    private final String encryptedPassword = "4Fhd6h5gs85dS";

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createUser_success() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());
        when(tokenService.generateEmailVerificationToken(ArgumentMatchers.anyString())).thenReturn(ArgumentMatchers.anyString());

        boolean created = registrationService.createUser(makeMockRegistrationRequest());

        assertTrue(created);
        verify(userRepo).findByEmail(ArgumentMatchers.anyString());
        verify(userRepo).findByUsername(ArgumentMatchers.anyString());
        verify(userRepo).save(ArgumentMatchers.any(User.class));
        verify(passwordEncoder).encode(password);
        verify(tokenService).generateEmailVerificationToken(ArgumentMatchers.anyString());
    }

    @Test
    void createUser_fail_usernameAlreadyExists() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(new User());
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        assertThrows(UserRegistrationException.class, () -> {
            registrationService.createUser(makeMockRegistrationRequest());
        });

        verify(userRepo).findByUsername(ArgumentMatchers.anyString());
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void createUser_fail_emailAlreadyExists() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(new User());
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        assertThrows(UserRegistrationException.class, () -> {
            registrationService.createUser(makeMockRegistrationRequest());
        });

        verify(userRepo).findByEmail(ArgumentMatchers.anyString());
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void createUser_fail_incorrectInputData() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(new User());
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());


        assertThrows(UserRegistrationException.class, () -> {
            boolean created = registrationService.createUser(new RegistrationRequest());
        });

        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void activateUser_success() {
        when(userRepo.findByActivationCode(ArgumentMatchers.anyString())).thenReturn(makeMockUser());
        when(tokenService.verifyToken(ArgumentMatchers.anyString())).thenReturn(true);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        boolean correctToken = registrationService.activateUser("correctToken");

        assertTrue(correctToken);
        verify(userRepo).findByActivationCode(ArgumentMatchers.anyString());
        verify(tokenService).verifyToken(ArgumentMatchers.anyString());
        verify(userRepo).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void activateUser_fail_incorrectActivationCode() {
        when(userRepo.findByActivationCode(ArgumentMatchers.anyString())).thenReturn(makeMockUser());
        when(tokenService.verifyToken(ArgumentMatchers.anyString())).thenReturn(false);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        boolean correctToken = registrationService.activateUser("incorrectToken");

        assertFalse(correctToken);
        verify(userRepo).findByActivationCode(ArgumentMatchers.anyString());
        verify(tokenService).verifyToken(ArgumentMatchers.anyString());
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void activateUser_fail_userWithActivationCodeNotFound() {
        when(userRepo.findByActivationCode(ArgumentMatchers.anyString())).thenReturn(null);
        when(tokenService.verifyToken(ArgumentMatchers.anyString())).thenReturn(true);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        boolean correctToken = registrationService.activateUser("incorrectToken");

        assertFalse(correctToken);
        verify(userRepo).findByActivationCode(ArgumentMatchers.anyString());
        verify(tokenService, times(0)).verifyToken(ArgumentMatchers.anyString());
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    private User makeMockUser() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }

    private RegistrationRequest makeMockRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        return request;
    }

}