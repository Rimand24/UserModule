package org.example.auth.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserLoginController {
    @GetMapping("/login")
    String login() {
        return "login";
    }
}