package org.example.auth.controller;

import org.example.auth.controller.requestDto.RegistrationForm;
import org.example.auth.service.registration.dto.RegistrationRequest;
import org.example.auth.service.registration.dto.RegistrationResponse;
import org.example.auth.service.registration.UserRegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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


//import org.example.auth.controller.requestDto.RegistrationForm;
//import org.example.auth.domain.User;
//import org.example.auth.service.UserService;
//import org.example.auth.service.registration.dto.RegistrationRequest;
//import org.example.auth.service.registration.dto.RegistrationResponse;
//import org.example.auth.service.registration.UserRegistrationService;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.validation.Valid;
//@Controller
//public class UserRegistrationController {
//
//    @Autowired
//    ModelMapper modelMapper;
//
//    @Autowired
//    UserRegistrationService userRegistrationService;
//
//    @GetMapping("/registration")
//    public String getRregistration(Model model) {
//        return "registration";
//    }
//
//    @PostMapping(value = "/registration")
//    public String postRegistration(RegistrationForm form, Model model) {
//
//        // System.out.println(form); //fixme
//
//        if (form == null || !form.getPassword().equals(form.getPassword2())) {
//            model.addAttribute("passwords does not match");
//            return "registration";
//        }
//
//
//
//
//
//        RegistrationRequest request = modelMapper.map(form, RegistrationRequest.class);
//        RegistrationResponse response = userRegistrationService.createUser(request);
//
//        if (response.isSuccess()) {
//            return "registrationSuccess";
//        } else {
//            model.addAllAttributes(response.getErrors());
//            return "registration";
//        }
//    }
//
//}