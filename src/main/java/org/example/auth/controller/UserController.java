package org.example.auth.controller;

import org.example.auth.service.user.UserDto;
import org.example.auth.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/{username}")
    public String getUser(@PathVariable String username, Model model) {
        UserDto dto = userService.getUserByUsername(username);
        model.addAttribute("user", dto);
        return "profile";
    }


}
