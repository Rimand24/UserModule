package org.example.auth.service.user;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.document.DocumentRequest;
import org.example.auth.service.document.DocumentServiceException;
import org.example.auth.service.registration.UserRegistrationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepo userRepo;

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
        when(userRepo.findByUsername(anyString())).thenReturn(mockUser);

        UserDetails user = userService.loadUserByUsername(anyString());

        assertNotNull(user);
        assertEquals(mockUser,user);
    }

    @Test
    void loadUserByUsername_fail_incorrectLogin() {
        when(userRepo.findByUsername(anyString())).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(anyString());
        });
    }
}