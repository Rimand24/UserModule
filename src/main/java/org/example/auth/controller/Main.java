package org.example.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class Main {

    @GetMapping("/")
    public String main(){
        return "index";
    }

    @GetMapping("/some")
    public String some(){
        return "index";
    }
}
