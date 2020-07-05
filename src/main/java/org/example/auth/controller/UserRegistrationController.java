package org.example.auth.controller;

import org.example.auth.controller.requestDto.RegistrationForm;
import org.example.auth.service.registration.RegistrationRequest;
import org.example.auth.service.registration.UserRegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserRegistrationController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRegistrationService userRegistrationService;

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        return "registration";
    }

    //todo: use Hibernate-Validation annotations to validate RegistrationForm
    // https://memorynotfound.com/spring-security-user-registration-example-thymeleaf/
    @PostMapping(value = "/registration")
    public String postRegistration(RegistrationForm form, Model model) {

        if (form == null || !StringUtils.hasText(form.getUsername()) || !StringUtils.hasText(form.getEmail())   //fixme extract to validator
                || !StringUtils.hasText(form.getPassword()) || !StringUtils.hasText(form.getPassword2())) {
            model.addAttribute("error", "fields does not filled");
            return "registration";
        }

        if (!form.getPassword().equals(form.getPassword2())) { //fixme extract to validator
            model.addAttribute("error", "passwords does not match");
            return "registration";
        }

        RegistrationRequest request = modelMapper.map(form, RegistrationRequest.class);
        boolean registrationDone = userRegistrationService.createUser(request);

        if (registrationDone) {
            return "registrationSuccess";
        } else {
            //model.addAllAttributes(response.getErrors()); //todo errors handling in registration html
            return "registration";
        }
    }

    @GetMapping("/activate/{activationCode}")
    public String activateUser(@PathVariable String activationCode) {
        boolean activated = userRegistrationService.activateUser(activationCode);
        if (activated) {
            return "login";
        }
        return "registration";
    }

}