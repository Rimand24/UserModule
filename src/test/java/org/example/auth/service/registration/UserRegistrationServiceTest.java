package org.example.auth.service.registration;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.registration.dto.RegistrationRequest;
import org.example.auth.service.registration.dto.RegistrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
class UserRegistrationServiceImplTest {

    @InjectMocks
    UserRegistrationServiceImpl registrationService;

    @Mock
    UserRepo userRepo;

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

        RegistrationResponse response = registrationService.createUser(makeMockRegistrationRequest());

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(Collections.EMPTY_SET, response.getErrors());
        verify(userRepo).findByEmail(ArgumentMatchers.anyString());
        verify(userRepo).findByUsername(ArgumentMatchers.anyString());
        verify(userRepo).save(ArgumentMatchers.any(User.class));
        verify(passwordEncoder).encode(password);
    }

    @Test
    void createUser_fail_usernameAlreadyExists() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(new User());
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        RegistrationResponse response = registrationService.createUser(makeMockRegistrationRequest());

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNotEquals(Collections.EMPTY_SET, response.getErrors());
        verify(userRepo).findByUsername(ArgumentMatchers.anyString());
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void createUser_fail_emailAlreadyExists() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(new User());
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        RegistrationResponse response = registrationService.createUser(makeMockRegistrationRequest());

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNotEquals(Collections.EMPTY_SET, response.getErrors());
        verify(userRepo).findByEmail(ArgumentMatchers.anyString());
        verify(userRepo, times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    void createUser_fail_incorectInputData() {
        when(userRepo.findByEmail(ArgumentMatchers.anyString())).thenReturn(new User());
        when(userRepo.findByUsername(ArgumentMatchers.anyString())).thenReturn(null);
        when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
        when(userRepo.save(ArgumentMatchers.any(User.class))).thenReturn(makeMockUser());

        RegistrationResponse response = registrationService.createUser(new RegistrationRequest());

        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNotEquals(Collections.EMPTY_SET, response.getErrors());
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

//    @Test
//    void createUser_success_withAddress() {
//        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
//        when(utils.generateUserId(ArgumentMatchers.anyInt())).thenReturn(userId);
//        when(bCryptPasswordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(encryptedPassword);
//        when(utils.generateEmailVerificationToken(ArgumentMatchers.anyString())).thenReturn(emailVerificationToken);
//        when(userRepository.save(ArgumentMatchers.any(UserEntity.class))).thenReturn(makeUserEntityWithAddresses());
//        //Mockito.when(emailService.sendEmailVerificationToken("", "", "")).thenReturn(true)
//        //emailService.sendEmailVerificationToken(user.getFirstName(), user.getEmail(), token);
//
//        UserDto userDto = makeUserDtoWithAddresses();
//
//        UserDto result = userService.createUser(userDto);
//        Assertions.assertNotNull(result);
//        Assertions.assertNotNull(result.getAddresses());
//        Assertions.assertEquals(firstName, result.getFirstName());
//        Assertions.assertEquals(encryptedPassword, result.getEncryptedPassword());
//        Assertions.assertEquals(emailVerificationToken, result.getEmailVerificationToken());
//        Assertions.assertEquals(email, result.getEmail());
//        Assertions.assertEquals(makeUserDtoWithAddresses().getAddresses().size(), result.getAddresses().size());
//        verify(utils, times(makeUserDtoWithAddresses().getAddresses().size())).generateAddressId(30);
//        verify(bCryptPasswordEncoder, times(1)).encode(password);
//        verify(userRepository, times(1)).save(ArgumentMatchers.any(UserEntity.class));
//    }
//
//    @Test
//    void createUser_fail_UserServiceException() {
//        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(new UserEntity());
//        Assertions.assertThrows(UserServiceException.class, () -> {
//                    userService.createUser(makeUserDto());
//                }
//                , ErrorMessages.RECORD_ALREADY_EXIST.getErrorMessage()
//        );
//    }
//
//    @Test
//    void getUser_success() {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUserId("4gfTJhKfg5");
//        userEntity.setFirstName("Alex");
//        userEntity.setLastName("Miller");
//        userEntity.setEncryptedPassword("4Fhd6h5gs85dS");
//        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(userEntity);
//
//        UserDto userDto = userService.getUser("email");
//        Assertions.assertNotNull(userDto);
//        Assertions.assertEquals("Alex", userDto.getFirstName());
//        Assertions.assertEquals("Miller", userDto.getLastName());
//        Assertions.assertEquals("4gfTJhKfg5", userDto.getUserId());
//        Assertions.assertEquals("4Fhd6h5gs85dS", userDto.getEncryptedPassword());
//        //Assertions.fail("not ready yet");
//    }
//
//    @Test
//    void getUser_fail_UsernameNotFoundException() {
//        when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(null);
//        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
//            userService.getUser("email");
//        });
//    }


}