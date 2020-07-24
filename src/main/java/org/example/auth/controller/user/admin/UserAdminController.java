package org.example.auth.controller.user.admin;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.service.user.admin.UserAdminService;
import org.example.auth.service.user.admin.dto.UserAdminResponse;
import org.example.auth.service.user.admin.dto.UserBlockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller
public class UserAdminController {

    @Autowired
    UserAdminService adminService;

    private static final String ADMIN_PAGE = "adminPage";
    private static final String REDIRECT_USERS_ACTIVATED_LIST = "redirect:/admin/users/all";

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ModelAndView getAdminPage() {
        return new ModelAndView(ADMIN_PAGE);
    }

    @PostMapping("/admin/users/block/")
    public ModelAndView blockUser(@AuthenticationPrincipal User user, @Valid UserBlockForm form) {
        UserBlockRequest request = new UserBlockRequest();
        request.setUsername(form.getUsername());
        request.setBlocker(user.getUsername());
        request.setReason(form.getReason());
        UserAdminResponse response = adminService.blockUser(request);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @PostMapping("/admin/users/unblock/")
    public ModelAndView unblockUser(@RequestParam @NotNull String username) {
        UserAdminResponse response = adminService.unblockUser(username);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @PostMapping("/admin/users/delete/")
    public ModelAndView deleteUser(@RequestParam @NotNull String username) {
        UserAdminResponse response = adminService.deleteUser(username);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @PostMapping("/admin/users/safeDelete/")
    public ModelAndView safeDeleteUser(@RequestParam @NotNull String username) {
        UserAdminResponse response = adminService.deleteUserOnly(username);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @GetMapping("/admin/users/addUserRole")
    public ModelAndView addUserRole(@RequestParam @NotNull String username, @NotNull Role role) {
        UserAdminResponse response = adminService.addUserRole(username, role);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @GetMapping("/admin/users/removeUserRole")
    public ModelAndView removeUserRole(@RequestParam @NotNull String username, @NotNull Role role) {
        UserAdminResponse response = adminService.addUserRole(username, role);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }
}
