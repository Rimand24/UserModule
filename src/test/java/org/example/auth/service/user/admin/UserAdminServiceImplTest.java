package org.example.auth.service.user.admin;

import org.example.auth.domain.Document;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.repo.UserRepo;
import org.example.auth.service.document.DocumentService;
import org.example.auth.service.user.admin.dto.UserAdminResponse;
import org.example.auth.service.user.admin.dto.UserBlockRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.example.auth.service.user.admin.dto.UserAdminServiceResponseCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserAdminServiceImplTest {

    @InjectMocks
    UserAdminServiceImpl adminService;

    @Mock
    UserRepo userRepo;

    @Mock
    DocumentService documentService;

    private static final String username = "Alex";
    private static final String email = "example@mail.com";
    private static final String password = "1234";

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void blockUser_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.blockUser(mockUserBlockRequest());

        verify(userRepo).findByUsername(anyString());
        verify(userRepo).save(any(User.class));
        assertTrue(response.isSuccess());
    }

    @Test
    void blockUser_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.blockUser(mockUserBlockRequest());

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void blockUser_fail_userAlreadyBlocked() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockBlockedUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.blockUser(mockUserBlockRequest());

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USER_ALREADY_BLOCKED, response.getStatus());
    }

    @Test
    void unblockUser_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockBlockedUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.unblockUser(username);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo).save(any(User.class));
        assertTrue(response.isSuccess());
    }

    @Test
    void unblockUser_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.unblockUser(username);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void unblockUser_fail_userNotBlocked() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.unblockUser(username);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USER_NOT_BLOCKED, response.getStatus());
    }

    @Test
    void deleteUser_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(documentService.deleteDocument(anyString())).thenReturn(true);
        doNothing().when(userRepo).delete(any(User.class));

        UserAdminResponse response = adminService.deleteUserWithUploads(username);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo).delete(any(User.class));
        assertTrue(response.isSuccess());
    }

    @Test
    void deleteUser_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(documentService.deleteDocument(anyString())).thenReturn(true);
        doNothing().when(userRepo).delete(any(User.class));

        UserAdminResponse response = adminService.deleteUserWithUploads(username);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).delete(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }


    @Test
    void deleteUserOnly_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(documentService.deleteDocument(anyString())).thenReturn(true);
        doNothing().when(userRepo).delete(any(User.class));

        UserAdminResponse response = adminService.deleteUserOnly(username);

        verify(userRepo).findByUsername(anyString());
        verify(documentService, times(0)).deleteDocument(anyString());
        assertTrue(response.isSuccess());
    }

    @Test
    void deleteUserOnly_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(documentService.deleteDocument(anyString())).thenReturn(true);
        doNothing().when(userRepo).delete(any(User.class));

        UserAdminResponse response = adminService.deleteUserOnly(username);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).delete(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void addUserRole_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.addUserRole(username, Role.ADMIN);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo).save(any(User.class));
        assertTrue(response.isSuccess());
    }

    @Test
    void addUserRole_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.addUserRole(username, Role.ADMIN);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void addUserRole_fail_userAlreadyHaveThisRole() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.addUserRole(username, Role.DOC_REDACTOR);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USER_ALREADY_HAVE_THIS_ROLE, response.getStatus());
    }

    @Test
    void removeUserRole_success() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.removeUserRole(username, Role.DOC_REDACTOR);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo).save(any(User.class));
        assertTrue(response.isSuccess());
    }

    @Test
    void removeUserRole_fail_basicRoleCannotBeRemoved() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));

        UserAdminResponse response = adminService.removeUserRole(username, Role.USER);

        verify(userRepo, times(0)).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(BASIC_USER_ROLE_CANNOT_BE_REMOVED, response.getStatus());
    }

    @Test
    void removeUserRole_fail_userNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.removeUserRole(username, Role.DOC_REDACTOR);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USERNAME_NOT_FOUND, response.getStatus());
    }

    @Test
    void removeUserRole_fail_userDontHaveThisRole() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(mockUser()));
        when(userRepo.save(any(User.class))).thenReturn(any(User.class));

        UserAdminResponse response = adminService.removeUserRole(username, Role.ADMIN);

        verify(userRepo).findByUsername(anyString());
        verify(userRepo, times(0)).save(any(User.class));
        assertEquals(USER_DO_NOT_HAVE_THIS_ROLE, response.getStatus());
    }

    private User mockUser() {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.DOC_REDACTOR);
        user.setAuthorities(roles);
        user.setAccountNonLocked(true);
        List<Document> list = new ArrayList<>();
        Document document = new Document();
        document.setDocId("id");
        list.add(document);
        user.setCreatedDocuments(list);
        return user;
    }

    private User mockBlockedUser() {
        User user = new User();
        user.setUsername(username);
        user.setAccountNonLocked(false);
        return user;
    }

    private UserBlockRequest mockUserBlockRequest() {
        UserBlockRequest request = new UserBlockRequest();
        request.setUsername(username);
        request.setBlocker("admin");
        request.setReason("block reason");
        return request;
    }
}