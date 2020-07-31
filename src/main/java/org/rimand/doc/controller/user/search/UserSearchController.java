package org.rimand.doc.controller.user.search;

import org.rimand.doc.domain.Role;
import org.rimand.doc.domain.User;
import org.rimand.doc.domain.dto.UserDto;
import org.rimand.doc.service.user.search.UserSearchService;
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

    private final UserSearchService userSearchService;

    //view names
    private static final String USER_LIST = "account/userList";
    private static final String PROFILE = "account/profile";
    private static final String USER_NOT_FOUND = "account/userNotFound";
    private static final String REDIRECT_GET_USER = "redirect:/users/";

    //models
    private static final String MODEL_NAME_USER = "user";
    private static final String MODEL_NAME_USERS = "userList";

    @Autowired
    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }


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
        return new ModelAndView(PROFILE, MODEL_NAME_USER, optional.get());
    }

    @GetMapping("/users/email/{email:.+}") //todo redirect to getUserByName with dto as param
    public ModelAndView getUserByEmail(@PathVariable String email) {
        Optional<UserDto> optional = userSearchService.findByEmail(email);
        if (optional.isEmpty()) {
            return new ModelAndView(USER_NOT_FOUND); //fixme
        }
        return new ModelAndView(PROFILE, MODEL_NAME_USER, optional.get());
    }


    //todo 1 method search with path params

    @GetMapping("/users/all")
    public ModelAndView getUsersActivated() {
        List<UserDto> list = userSearchService.findAllActivated();
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, list);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/total")
    public ModelAndView getUsersAll() {
        List<UserDto> list = userSearchService.findAll();
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, list);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/notActivated")
    public ModelAndView getUsersNotActivated() {
        List<UserDto> list = userSearchService.findAllNotActivated();
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, list);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/blocked")
    public ModelAndView getUsersBlocked() {
        List<UserDto> list = userSearchService.findAllBlocked();
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, list);
    }

    @PostMapping("/users/byRole")
    public ModelAndView getUsersByRole(@RequestParam Role role) {
        List<UserDto> users = userSearchService.findAllByRole(role);
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, users);
    }

    @GetMapping("/users/search")
    public ModelAndView searchUsersRefreshPage(String username) {
        username = username == null? "" : username;

        List<UserDto> users = userSearchService.searchUsersByName(username);
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, users);
    }

    @PostMapping ("/users/search")
    public ModelAndView searchUsers(@RequestParam String username) {
        List<UserDto> users = userSearchService.searchUsersByName(username);
        return new ModelAndView(USER_LIST, MODEL_NAME_USERS, users);
    }

}