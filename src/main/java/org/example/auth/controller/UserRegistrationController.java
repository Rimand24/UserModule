package org.example.auth.controller;

import org.example.auth.controller.requestDto.RegistrationForm;
import org.example.auth.service.registration.RegistrationRequest;
import org.example.auth.service.registration.RegistrationResponse;
import org.example.auth.service.registration.UserRegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class UserRegistrationController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRegistrationService userRegistrationService;

    @GetMapping("/registration")
    public String getRregistration(Model model) {
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String postRegistration(RegistrationForm form, Model model) {

       // System.out.println(form); //fixme

        if (form == null || !form.getPassword().equals(form.getPassword2())) {
            model.addAttribute("passwords does not match");
            return "registration";
        }





            RegistrationRequest request = modelMapper.map(form, RegistrationRequest.class);
        RegistrationResponse response = userRegistrationService.createUser(request);

        if (response.isSuccess()) {
            return "registrationSuccess";
        } else {
            model.addAllAttributes(response.getErrors());
            return "registration";
        }
    }

}
