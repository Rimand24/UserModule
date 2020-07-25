package org.example.auth.controller.user.search;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.domain.UserDto;
import org.example.auth.service.user.search.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class UserSearchController {
    @Autowired
    UserSearchService userSearchService;

    private static final String USER_LIST = "userList";
    private static final String PROFILE = "account/profile";
    private static final String USER_NOT_FOUND = "userNotFound";
    private static final String REDIRECT_GET_USER = "redirect:/users/";

    @GetMapping("/users")
    public String getUserSelf(@AuthenticationPrincipal User user) {
        return REDIRECT_GET_USER + user.getUsername();
    }

    @GetMapping("/users/{username}")
    public ModelAndView getUserByName(@PathVariable String username) {
        Optional<UserDto> optional = userSearchService.findByUsername(username);
        if (optional.isEmpty()) {
            return new ModelAndView(USER_NOT_FOUND); //fixme
        }
        return new ModelAndView(PROFILE, "user", optional.get());
    }

    @GetMapping("/users/{email:.+}") //todo redirect to getUserByName with dto as param
    public ModelAndView getUserByEmail(@PathVariable String email) {
        Optional<UserDto> optional = userSearchService.findByEmail(email);
        if (optional.isEmpty()) {
            return new ModelAndView(USER_NOT_FOUND); //fixme
        }
        return new ModelAndView(PROFILE, "user", optional.get());
    }

    @GetMapping("/users/all")
    public ModelAndView getUsersActivated() {
        List<UserDto> list = userSearchService.findAllActivated();
        return new ModelAndView(USER_LIST, "userList", list);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/total")
    public ModelAndView getUsersAll() {
        List<UserDto> list = userSearchService.findAll();
        return new ModelAndView(USER_LIST, "userList", list);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/notActivated")
    public ModelAndView getUsersNotActivated() {
        List<UserDto> list = userSearchService.findAllNotActivated();
        return new ModelAndView(USER_LIST, "userList", list);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/blocked")
    public ModelAndView getUsersBlocked() {
        List<UserDto> list = userSearchService.findAllBlocked();
        return new ModelAndView(USER_LIST, "userList", list);
    }

    @PostMapping("/users/byRole")
    public ModelAndView getUsersByRole(@RequestParam Role role) {
        List<UserDto> users = userSearchService.findAllByRole(role);
        return new ModelAndView(USER_LIST, "userList", users);
    }

    @PostMapping("/users/search")
    public ModelAndView searchUsers(@RequestParam String name) {
        List<UserDto> users = userSearchService.searchUsersByName(name);
        return new ModelAndView(USER_LIST, "userList", users);
    }

}