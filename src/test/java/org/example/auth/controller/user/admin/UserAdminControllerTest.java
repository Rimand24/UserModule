package org.example.auth.controller.user.admin;

import org.example.auth.domain.Role;
import org.example.auth.service.user.admin.UserAdminService;
import org.example.auth.service.user.admin.dto.UserAdminResponse;
import org.example.auth.service.user.admin.dto.UserBlockRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.example.auth.service.user.account.dto.UserAccountServiceResponseCode.OK;
import static org.example.auth.service.user.admin.dto.UserAdminServiceResponseCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserAdminController.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService adminService;

    private static final String ADMIN_PAGE = "adminPage";
    private static final String REDIRECT_USERS_ACTIVATED_LIST = "redirect:/admin/users/all";

    private static final String username = "Alex";
    private static final String reason = "block reason";


    @Test
    void getAdminPage_success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name(ADMIN_PAGE));
    }

    @Test
    void blockUser_success() throws Exception {
        when(adminService.blockUser(any(UserBlockRequest.class))).thenReturn(new UserAdminResponse(OK));

        //@AuthenticationPrincipal User user

        mockMvc.perform(post("/admin/users/block/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("reason", reason))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"));

        verify(adminService).blockUser(any(UserBlockRequest.class));
    }

    @Test
    void blockUser_fail_userNotFound() throws Exception {
        when(adminService.blockUser(any(UserBlockRequest.class))).thenReturn(new UserAdminResponse(USERNAME_NOT_FOUND));

        //@AuthenticationPrincipal User user

        mockMvc.perform(post("/admin/users/block/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("reason", reason))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USERNAME_NOT_FOUND));

        verify(adminService).blockUser(any(UserBlockRequest.class));
    }

    @Test
    void blockUser_fail_userAlreadyBlocked() throws Exception {
        when(adminService.blockUser(any(UserBlockRequest.class))).thenReturn(new UserAdminResponse(USER_ALREADY_BLOCKED));

        //@AuthenticationPrincipal User user

        mockMvc.perform(post("/admin/users/block/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("reason", reason))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USER_ALREADY_BLOCKED));

        verify(adminService).blockUser(any(UserBlockRequest.class));
    }

    @Test
    void unblockUser_success() throws Exception {
        when(adminService.unblockUser(anyString())).thenReturn(new UserAdminResponse(OK));

        //@AuthenticationPrincipal User user

        mockMvc.perform(post("/admin/users/unblock/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"));

        verify(adminService).unblockUser(anyString());
    }

    @Test
    void unblockUser_fail_userNotFound() throws Exception {
        when(adminService.unblockUser(anyString())).thenReturn(new UserAdminResponse(USERNAME_NOT_FOUND));

        //@AuthenticationPrincipal User user

        mockMvc.perform(post("/admin/users/unblock/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USERNAME_NOT_FOUND));

        verify(adminService).unblockUser(anyString());
    }

    @Test
    void unblockUser_fail_userNotBlocked() throws Exception {
        when(adminService.unblockUser(anyString())).thenReturn(new UserAdminResponse(USER_NOT_BLOCKED));

        //@AuthenticationPrincipal User user

        mockMvc.perform(post("/admin/users/unblock/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USER_NOT_BLOCKED));

        verify(adminService).unblockUser(anyString());
    }

    @Test
    void deleteUser_success() throws Exception {
        when(adminService.deleteUserWithUploads(anyString())).thenReturn(new UserAdminResponse(OK));

        mockMvc.perform(post("/admin/users/delete/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"));
        //  .andExpect(MockMvcResultMatchers.model().attribute("error", USER_NOT_BLOCKED));

        verify(adminService).deleteUserWithUploads(anyString());
    }

    @Test
    void deleteUser_fail_userNotFound() throws Exception {
        when(adminService.deleteUserWithUploads(anyString())).thenReturn(new UserAdminResponse(USERNAME_NOT_FOUND));

        mockMvc.perform(post("/admin/users/delete/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USERNAME_NOT_FOUND));

        verify(adminService).deleteUserWithUploads(anyString());
    }

    @Test
    void safeDeleteUser_success() throws Exception {
        when(adminService.deleteUserOnly(anyString())).thenReturn(new UserAdminResponse(OK));

        mockMvc.perform(post("/admin/users/safeDelete/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"));

        verify(adminService).deleteUserOnly(anyString());
    }

    @Test
    void safeDeleteUser_userNotFound() throws Exception {
        when(adminService.deleteUserOnly(anyString())).thenReturn(new UserAdminResponse(USERNAME_NOT_FOUND));

        mockMvc.perform(post("/admin/users/safeDelete/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USERNAME_NOT_FOUND));

        verify(adminService).deleteUserOnly(anyString());
    }

    @Test
    void addUserRole_success() throws Exception {
        when(adminService.addUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(OK));

        mockMvc.perform(post("/admin/users/addUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"));

        verify(adminService).addUserRole(anyString(), any(Role.class));
    }

    @Test
    void addUserRole_fail_userNotFound() throws Exception {
        when(adminService.addUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(USERNAME_NOT_FOUND));

        mockMvc.perform(post("/admin/users/addUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USERNAME_NOT_FOUND));

        verify(adminService).addUserRole(anyString(), any(Role.class));
    }

    @Test
    void addUserRole_fail_AlreadyHaveThisRole() throws Exception {
        when(adminService.addUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(USER_ALREADY_HAVE_THIS_ROLE));

        mockMvc.perform(post("/admin/users/addUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USER_ALREADY_HAVE_THIS_ROLE));

        verify(adminService).addUserRole(anyString(), any(Role.class));
    }

    @Test
    void removeUserRole_success() throws Exception {
        when(adminService.removeUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(OK));

        mockMvc.perform(post("/admin/users/removeUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("error"));

        verify(adminService).removeUserRole(anyString(), any(Role.class));
    }

    @Test
    void removeUserRole_fail_basicRoleCannotBeRemoved() throws Exception {
        when(adminService.removeUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(BASIC_USER_ROLE_CANNOT_BE_REMOVED));

        mockMvc.perform(post("/admin/users/removeUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", BASIC_USER_ROLE_CANNOT_BE_REMOVED));

        verify(adminService, times(0)).removeUserRole(anyString(), any(Role.class));
    }

    @Test
    void removeUserRole_fail_userNotFound() throws Exception {
        when(adminService.removeUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(USERNAME_NOT_FOUND));

        mockMvc.perform(post("/admin/users/removeUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USERNAME_NOT_FOUND));

        verify(adminService).removeUserRole(anyString(), any(Role.class));
    }

    @Test
    void removeUserRole_fail_userDontHaveThisRole() throws Exception {
        when(adminService.removeUserRole(anyString(), any(Role.class))).thenReturn(new UserAdminResponse(USER_DO_NOT_HAVE_THIS_ROLE));

        mockMvc.perform(post("/admin/users/removeUserRole")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name(REDIRECT_USERS_ACTIVATED_LIST))
                .andExpect(MockMvcResultMatchers.model().attribute("error", USER_DO_NOT_HAVE_THIS_ROLE));

        verify(adminService).removeUserRole(anyString(), any(Role.class));
    }
}