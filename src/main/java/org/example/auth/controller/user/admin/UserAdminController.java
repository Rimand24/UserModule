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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@PreAuthorize("hasAuthority('ADMIN')")
@Controller
public class UserAdminController {

    @Autowired
    UserAdminService adminService;

    private static final String ADMIN_PAGE = "adminPage";
    private static final String BLOCK_FORM = "blockForm";
    private static final String REDIRECT_USERS_ACTIVATED_LIST = "redirect:/users/all";

    @GetMapping("/admin")
    public ModelAndView getAdminPage() {
        return new ModelAndView(ADMIN_PAGE);
    }

    @GetMapping("/admin/users/block")
    public ModelAndView getBlockPage() {
        return new ModelAndView(BLOCK_FORM);
    }

    @PostMapping("/admin/users/block")
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

    @GetMapping("/admin/users/unblock/{username}")
    public ModelAndView unblockUserPathVar(@PathVariable String username) {
        return unblockUser(username);
    }

    @PostMapping("/admin/users/unblock")
    public ModelAndView unblockUser(@RequestParam @NotNull String username) {
        UserAdminResponse response = adminService.unblockUser(username);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @PostMapping("/admin/users/delete")
    public ModelAndView deleteUserWithDocuments(@RequestParam @NotNull String username) {
        UserAdminResponse response = adminService.deleteUserWithUploads(username);
        if (!response.isSuccess()) {
            return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST, "error", response.getStatus());
        }
        return new ModelAndView(REDIRECT_USERS_ACTIVATED_LIST);
    }

    @PostMapping("/admin/users/safeDelete")
    public ModelAndView deleteUserOnly(@RequestParam @NotNull String username) {
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
