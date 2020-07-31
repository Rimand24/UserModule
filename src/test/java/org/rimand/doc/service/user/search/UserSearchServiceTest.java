package org.rimand.doc.service.user.search;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rimand.doc.domain.User;
import org.rimand.doc.domain.dto.UserDto;
import org.rimand.doc.repo.UserRepo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserSearchServiceTest {

    @InjectMocks
    UserSearchService userSearchService;

    @Mock
    UserRepo userRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    private static final String username = "username";
    private static final String email = "email@example.com";

    @Test
    void findByUsername_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));

        Optional<UserDto> user = userSearchService.findByUsername(username);

        verify(userRepo).findByUsername(anyString());
        assertEquals(username, user.get().getUsername());
    }

    @Test
    void findByUsername_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        Optional<UserDto> user = userSearchService.findByUsername(username);

        verify(userRepo).findByUsername(anyString());
        assertTrue(user.isEmpty());
    }

    @Test
    void findByEmail_success() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(mockUser()));

        Optional<UserDto> user = userSearchService.findByEmail(email);

        verify(userRepo).findByEmail(anyString());
        assertEquals(email, user.get().getEmail());
    }

    @Test
    void findByEmail_fail_userNotFound() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<UserDto> user = userSearchService.findByEmail(email);

        verify(userRepo).findByEmail(anyString());
        assertTrue(user.isEmpty());
    }

    @Test
    void findAll_success() {
        when(userRepo.findAll()).thenReturn(mockUserList());

        List<UserDto> list = userSearchService.findAll();

        verify(userRepo).findAll();
        assertEquals(mockUserDtoList(), list);
    }

    @Test
    void findAll_emptyList() {
        when(userRepo.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> list = userSearchService.findAll();

        verify(userRepo).findAll();
        assertEquals(Collections.EMPTY_LIST, list);
    }


    @Test
    void findAllActivated_success() {
        when(userRepo.findAllByEnabledTrue()).thenReturn(mockUserList());

        List<UserDto> list = userSearchService.findAllActivated();

        verify(userRepo).findAllByEnabledTrue();
        assertEquals(mockUserDtoList(), list);
    }

    @Test
    void findAllActivated_emptyList() {
        when(userRepo.findAllByEnabledTrue()).thenReturn(Collections.emptyList());

        List<UserDto> list = userSearchService.findAllActivated();

        verify(userRepo).findAllByEnabledTrue();
        assertEquals(Collections.EMPTY_LIST, list);
    }


    @Test
    void findAllNotActivated_success() {
        when(userRepo.findAllByEnabledFalse()).thenReturn(mockUserList());

        List<UserDto> list = userSearchService.findAllNotActivated();

        verify(userRepo).findAllByEnabledFalse();
        assertEquals(mockUserDtoList(), list);

    }

    @Test
    void findAllNotActivated_emptyList() {
        when(userRepo.findAllByEnabledFalse()).thenReturn(Collections.emptyList());

        List<UserDto> list = userSearchService.findAllNotActivated();

        verify(userRepo).findAllByEnabledFalse();
        assertEquals(Collections.EMPTY_LIST, list);
    }


    @Test
    void findAllBlocked_success() {
        when(userRepo.findAllByAccountNonLockedFalse()).thenReturn(mockUserList());

        List<UserDto> list = userSearchService.findAllBlocked();

        verify(userRepo).findAllByAccountNonLockedFalse();
        assertEquals(mockUserDtoList(), list);
    }

    @Test
    void findAllBlocked_emptyList() {
        when(userRepo.findAllByAccountNonLockedFalse()).thenReturn(Collections.emptyList());

        List<UserDto> list = userSearchService.findAllBlocked();

        verify(userRepo).findAllByAccountNonLockedFalse();
        assertEquals(Collections.EMPTY_LIST, list);
    }

    @Test
    void searchUsersByName_success() {
        when(userRepo.findAllByUsernameContains(anyString())).thenReturn(Collections.emptyList());

        List<UserDto> list = userSearchService.findAllBlocked();

        verify(userRepo).findAllByUsernameContains(anyString());
        assertEquals(mockUserDtoList(), list);
    }

    @Test
    void searchUsersByName_emptyList() {
        when(userRepo.findAllByUsernameContains(anyString())).thenReturn(Collections.emptyList());

        List<UserDto> list = userSearchService.findAllBlocked();

        verify(userRepo).findAllByUsernameContains(anyString());
        assertEquals(Collections.EMPTY_LIST, list);
    }

    private User mockUser() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    private UserDto mockUserDto() {
        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setEmail(email);
        return dto;
    }

    private List<User> mockUserList() {
        List<User> list = new ArrayList<>();
        list.add(mockUser());
        list.add(mockUser());
        return list;
    }

    private List<UserDto> mockUserDtoList() {
        List<UserDto> list = new ArrayList<>();
        list.add(mockUserDto());
        list.add(mockUserDto());
        return list;
    }

}