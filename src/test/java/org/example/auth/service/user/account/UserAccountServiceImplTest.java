package org.example.auth.service.user.account;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.mail.Mail;
import org.example.auth.service.mail.MailService;
import org.example.auth.service.user.account.request.RegistrationRequest;
import org.example.auth.service.util.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserAccountServiceImplTest {

    @InjectMocks
    UserAccountServiceImpl accountService;

    @Mock
    UserRepo userRepo;

    @Mock
    TokenService tokenService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    MailService mailService;

    private final String username = "Alex";
    private final String email = "example@mail.com";
    private final String password = "1234";
    private final String encryptedPassword = "4Fhd6h5gs85dS";

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void loadUserByUsername_success() {
        User mockUser = new User(){{
            setUsername("username");
            setPassword("pass");
            setEmail("mail@mail.pp");
        }};
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        UserDetails user = accountService.loadUserByUsername(anyString());

        assertNotNull(user);
        assertEquals(mockUser,user);
    }

    @Test
    void loadUserByUsername_fail_incorrectLogin() {
        when(userRepo.findByUsername(anyString())).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            accountService.loadUserByUsername(anyString());
        });
    }


    @Test
    void createUser_success() {
        when(userRepo.findByEmail(anyString())).thenReturn(null);
        when(userRepo.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(tokenService.generateEmailVerificationToken()).thenReturn("anyString");
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());
        doNothing().when(mailService).send(any(Mail.class));

        boolean created = accountService.createUser(makeMockRegistrationRequest());

        assertTrue(created);
        verify(userRepo).findByEmail(anyString());
        verify(userRepo).findByUsername(anyString());
        verify(userRepo).save(any(User.class));
        verify(passwordEncoder).encode(password);
        verify(tokenService).generateEmailVerificationToken();
    }

    @Test
    void createUser_fail_usernameAlreadyExists() {
        when(userRepo.findByEmail(anyString())).thenReturn(null);
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());

        assertThrows(UserAccountServiceException.class, () -> {
            accountService.createUser(makeMockRegistrationRequest());
        });

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void createUser_fail_emailAlreadyExists() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(userRepo.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());

        assertThrows(UserAccountServiceException.class, () -> {
            accountService.createUser(makeMockRegistrationRequest());
        });

        verify(userRepo).findByEmail(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void createUser_fail_incorrectInputData() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(userRepo.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());

        assertThrows(UserAccountServiceException.class, () -> {
            accountService.createUser(new RegistrationRequest());
        });

        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void activateUser_success() {
        when(userRepo.findByEmailActivationCode(anyString())).thenReturn(Optional.of(makeMockUser()));
        when(tokenService.verifyToken(anyString())).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());

        boolean correctToken = accountService.activateUser(anyString());

        assertTrue(correctToken);
        verify(userRepo).findByEmailActivationCode(anyString());
        verify(tokenService).verifyToken(anyString());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void activateUser_fail_incorrectActivationCode() {
        when(userRepo.findByEmailActivationCode(anyString())).thenReturn(Optional.of(makeMockUser()));
        when(tokenService.verifyToken(anyString())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());

        boolean correctToken = accountService.activateUser("incorrectToken");

        assertFalse(correctToken);
        verify(userRepo).findByEmailActivationCode(anyString());
        verify(tokenService).verifyToken(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void activateUser_fail_userWithActivationCodeNotFound() {
        when(userRepo.findByEmailActivationCode(anyString())).thenReturn(null);
        when(tokenService.verifyToken(anyString())).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(makeMockUser());

        boolean correctToken = accountService.activateUser("incorrectToken");

        assertFalse(correctToken);
        verify(userRepo).findByEmailActivationCode(anyString());
        verify(tokenService, times(0)).verifyToken(anyString());
        verify(userRepo, times(0)).save(any(User.class));
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