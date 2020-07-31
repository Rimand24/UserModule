package org.rimand.doc.service.user.account;



import org.rimand.doc.domain.User;
import org.rimand.doc.repo.UserRepo;
import org.rimand.doc.service.mail.MailService;
import org.rimand.doc.service.user.account.dto.*;
import org.rimand.doc.service.util.RandomGeneratorUtils;
import org.rimand.doc.service.util.TokenService;
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
import static org.rimand.doc.service.user.account.dto.UserAccountServiceResponseCode.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserAccountServiceTest {

    @InjectMocks
    UserAccountService accountService;

    @Mock
    UserRepo userRepo;

    @Mock
    TokenService tokenService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RandomGeneratorUtils generator;

    @Mock
    MailService mailService;

    private static final String username = "Alex";
    private static final String email = "example@mail.com";
    private static final String newEmail = "newEmail@mail.com";
    private static final String password = "1234";
    private static final String newPassword = "newPassword";
    private static final String encryptedPassword = "4Fhd6h5gs85dS";
    private static final String TOKEN = "token.with.dots";

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadUserByUsername_success() {

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));

        UserDetails user = accountService.loadUserByUsername(username);

        assertNotNull(user);
        assertEquals(mockUser(), user);
    }

    @Test
    void loadUserByUsername_fail_incorrectLogin() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            accountService.loadUserByUsername(anyString());
        });
    }


    @Test
    void createUser_success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(tokenService.generateEmailVerificationToken()).thenReturn("anyString");
        when(userRepo.save(any(User.class))).thenReturn(mockUser());
        when(mailService.sendActivationCode(anyString(), anyString(), anyString())).thenReturn(null);

        UserAccountResponse response = accountService.createUser(mockRegistrationRequest());

        assertTrue(response.isSuccess());
        verify(userRepo).findByEmail(anyString());
        verify(userRepo).findByUsername(anyString());
        verify(userRepo).save(any(User.class));
        verify(passwordEncoder).encode(password);
        verify(tokenService).generateEmailVerificationToken();
    }

    @Test
    void createUser_fail_usernameAlreadyExists() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(any(User.class))).thenReturn(mockUser());

        UserAccountResponse response = accountService.createUser(mockRegistrationRequest());
        assertEquals(response.getStatus(), UserAccountServiceResponseCode.USERNAME_ALREADY_EXISTS);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void createUser_fail_emailAlreadyExists() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(any(User.class))).thenReturn(mockUser());

        UserAccountResponse response = accountService.createUser(mockRegistrationRequest());
        assertEquals(response.getStatus(), UserAccountServiceResponseCode.EMAIL_ALREADY_EXISTS);

        verify(userRepo).findByEmail(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void activateUser_success() {
        when(userRepo.findByEmailActivationCode(anyString())).thenReturn(Optional.of(mockUser()));
        when(tokenService.verifyToken(anyString())).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(mockUser());

        UserAccountResponse response = accountService.activateUser(anyString());

        assertTrue(response.isSuccess());
        verify(userRepo).findByEmailActivationCode(anyString());
        verify(tokenService).verifyToken(anyString());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void activateUser_fail_incorrectActivationCode() {
        when(userRepo.findByEmailActivationCode(anyString())).thenReturn(Optional.of(mockUser()));
        when(tokenService.verifyToken(anyString())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAccountResponse response = accountService.activateUser("incorrectToken");

        assertFalse(response.isSuccess());
        assertEquals(response.getStatus(), UserAccountServiceResponseCode.TOKEN_NOT_VERIFIED);
        verify(userRepo).findByEmailActivationCode(anyString());
        verify(tokenService).verifyToken(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void activateUser_fail_userWithActivationCodeNotFound() {
        when(userRepo.findByEmailActivationCode(anyString())).thenReturn(Optional.empty());
        when(tokenService.verifyToken(anyString())).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAccountResponse response = accountService.activateUser("incorrectToken");

        assertFalse(response.isSuccess());
        assertEquals(response.getStatus(), UserAccountServiceResponseCode.EMAIL_TOKEN_NOT_FOUND);
        verify(userRepo).findByEmailActivationCode(anyString());
        verify(tokenService, times(0)).verifyToken(anyString());
        verify(userRepo, times(0)).save(any(User.class));
    }

    @Test
    void resendActivationCode_success() {
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(mockUserWithActivationCode()));
        when(tokenService.generateEmailVerificationToken()).thenReturn(TOKEN);
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAccountResponse response = accountService.resendActivationCode(email);

        verify(userRepo).findByEmail(email);
        verify(tokenService).generateEmailVerificationToken();
        verify(userRepo).save(any(User.class));
        assertTrue(response.isSuccess());
    }


    @Test
    void resendActivationCode_fail_emailNotFound() {
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        UserAccountResponse response = accountService.resendActivationCode(email);

        verify(userRepo).findByEmail(email);
        verify(tokenService, times(0)).generateEmailVerificationToken();
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(EMAIL_NOT_FOUND, response.getStatus());
    }

    @Test
    void resendActivationCode_fail_tokenIncorrect() {
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(mockUser()));

        UserAccountResponse response = accountService.resendActivationCode(email);

        verify(userRepo).findByEmail(email);
        verify(tokenService, times(0)).generateEmailVerificationToken();
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(ALREADY_ACTIVATED, response.getStatus());
    }

    @Test
    void changePassword_success() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(mockUser());
        when(passwordEncoder.encode(password)).thenReturn(password);

        UserAccountResponse response = accountService.changePassword(mockChangePasswordRequest());

        verify(userRepo).findByUsername(username);
        assertTrue(response.isSuccess());
    }

    @Test
    void changePassword_fail_userNotFound() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAccountResponse response = accountService.changePassword(mockChangePasswordRequest());

        verify(userRepo).findByUsername(username);
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void changePassword_fail_userPasswordIncorrect() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));
        when(passwordEncoder.encode(password)).thenReturn(password);

        UserAccountResponse response = accountService.changePassword(mockChangePasswordRequest());

        verify(userRepo).findByUsername(username);
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(PASSWORD_INCORRECT, response.getStatus());
    }

    @Test
    void sendResetPasswordCode_success() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(mailService.sendResetPasswordCode(anyString(), anyString(), anyString())).thenReturn(null);
        when(tokenService.generatePasswordResetToken()).thenReturn(TOKEN);

        UserAccountResponse response = accountService.sendResetPasswordCode(username);

        verify(userRepo).findByUsername(username);
        verify(mailService).sendResetPasswordCode(username, email, TOKEN);
        assertTrue(response.isSuccess());
    }

    @Test
    void sendResetPasswordCode_fail_userNotFound() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        UserAccountResponse response = accountService.sendResetPasswordCode(username);

        verify(userRepo).findByUsername(username);
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void resetPassword_success() {
        when(userRepo.findByPasswordResetCode(TOKEN)).thenReturn(Optional.of(mockUser()));
        when(tokenService.verifyToken(TOKEN)).thenReturn(true);
        when(userRepo.save(any(User.class))).thenReturn(mockUser());
        when(mailService.sendResetPasswordCode(anyString(), anyString(), anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(generator.generatePassword()).thenReturn("newPassword");

        UserAccountResponse response = accountService.resetPassword(TOKEN);

        verify(mailService).sendNewPassword(username, email, "newPassword");
        verify(userRepo).findByPasswordResetCode(TOKEN);
        verify(userRepo).save(any(User.class));
        assertTrue(response.isSuccess());
    }

    @Test
    void resetPassword_fail_tokenNotFound() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        UserAccountResponse response = accountService.resetPassword(TOKEN);

        verify(userRepo).findByPasswordResetCode(TOKEN);
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(PASSWORD_TOKEN_NOT_FOUND, response.getStatus());
    }

    @Test
    void resetPassword_fail_tokenNotVerified() {
        when(userRepo.findByPasswordResetCode(TOKEN)).thenReturn(Optional.of(mockUser()));
        when(tokenService.verifyToken(TOKEN)).thenReturn(false);

        UserAccountResponse response = accountService.resetPassword(TOKEN);

        verify(userRepo).findByPasswordResetCode(TOKEN);
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(TOKEN_NOT_VERIFIED, response.getStatus());
    }

    @Test
    void changeEmail_success() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(mockUser())).thenReturn(mockUser());
        when(tokenService.generateEmailVerificationToken()).thenReturn(TOKEN);
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(mailService.sendNewEmailConfirmCode(anyString(), anyString(), anyString())).thenReturn(null);

        UserAccountResponse response = accountService.changeEmail(mockChangeEmailRequest());

        verify(userRepo).findByUsername(anyString());
        verify(userRepo).findByEmail(anyString());
        verify(userRepo).save(any(User.class));
        verify(mailService).sendNewEmailConfirmCode(anyString(), anyString(), anyString());
        assertTrue(response.isSuccess());
    }

    @Test
    void changeEmail_fail_userNotFound() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
        when(tokenService.generateEmailVerificationToken()).thenReturn(TOKEN);
        when(userRepo.save(mockUser())).thenReturn(mockUser());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        UserAccountResponse response = accountService.changeEmail(mockChangeEmailRequest());

        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void changeEmail_fail_passwordIncorrect() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(tokenService.generateEmailVerificationToken()).thenReturn(TOKEN);
        when(userRepo.save(mockUser())).thenReturn(mockUser());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(password);

        UserAccountResponse response = accountService.changeEmail(mockChangeEmailRequestWithIncorrectPassword());

        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(PASSWORD_INCORRECT, response.getStatus());
    }

    @Test
    void changeEmail_fail_emailTheSame() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(tokenService.generateEmailVerificationToken()).thenReturn(TOKEN);
        when(userRepo.save(mockUser())).thenReturn(mockUser());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser()));
        when(passwordEncoder.encode(password)).thenReturn(password);

        UserAccountResponse response = accountService.changeEmail(mockChangeEmailRequestWithSameEmail());

        verify(userRepo).findByUsername(username);
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(NEW_EMAIL_IS_THE_SAME, response.getStatus());
    }

    @Test
    void changeEmail_fail_emailAlreadyBusy() {
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(mockUser()));
        when(tokenService.generateEmailVerificationToken()).thenReturn(TOKEN);
        when(userRepo.save(mockUser())).thenReturn(mockUser());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser()));
        when(passwordEncoder.encode(password)).thenReturn(password);

        UserAccountResponse response = accountService.changeEmail(mockChangeEmailRequest());

        verify(userRepo).findByUsername(username);
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(EMAIL_ALREADY_EXISTS, response.getStatus());
    }

    @Test
    void confirmChangeEmail_success() {
        when(userRepo.findByEmailActivationCode(TOKEN)).thenReturn(Optional.of(mockUser()));
        when(tokenService.getEmailFromToken(TOKEN)).thenReturn(email);
        when(userRepo.save(mockUser())).thenReturn(mockUser());
        when(mailService.sendNewEmailConfirmSuccess(username, email)).thenReturn(null);

        UserAccountResponse response = accountService.confirmChangeEmail(TOKEN);

        verify(userRepo).save(any(User.class));
        verify(tokenService).getEmailFromToken(anyString());
        verify(mailService).sendNewEmailConfirmSuccess(username, email);
        assertTrue(response.isSuccess());
    }

    @Test
    void confirmChangeEmail_fail_tokenNotFound() {
        when(userRepo.findByEmailActivationCode(TOKEN)).thenReturn(Optional.empty());

        UserAccountResponse response = accountService.confirmChangeEmail(TOKEN);

        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(EMAIL_TOKEN_NOT_FOUND, response.getStatus());
    }

    private RegistrationRequest mockRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        return request;
    }

    private User mockUser() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }

    private User mockUserWithActivationCode() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setEmailActivationCode(TOKEN);
        return user;
    }

    private ChangePasswordRequest mockChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername(username);
        request.setOldPassword(password);
        request.setPassword(newPassword);
        return request;
    }

    private ChangeEmailRequest mockChangeEmailRequest() {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setUsername(username);
        request.setEmail(newEmail);
        request.setPassword(password);
        return request;
    }

    private ChangeEmailRequest mockChangeEmailRequestWithIncorrectPassword() {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setUsername(username);
        request.setEmail(newEmail);
        request.setPassword(newPassword);
        return request;
    }

    private ChangeEmailRequest mockChangeEmailRequestWithSameEmail() {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}