package org.example.auth.service.user;

import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.user.search.UserSearchServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class UserSearchServiceImplTest {

    @InjectMocks
    UserSearchServiceImpl userService;

    @Mock
    UserRepo userRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }


}