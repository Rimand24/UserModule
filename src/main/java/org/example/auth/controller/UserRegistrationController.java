package org.example.auth.controller;

import org.example.auth.controller.requestDto.RegistrationForm;
import org.example.auth.service.registration.RegistrationRequest;
import org.example.auth.service.registration.UserRegistrationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserRegistrationController {

    private static final String REGISTRATION_PAGE = "registration";
    private static final String REGISTRATION_SUCCESS = "registrationSuccess";

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRegistrationService userRegistrationService;

    @GetMapping("/registration")
    public String showRegistrationForm() {
        return REGISTRATION_PAGE;
    }

    @PostMapping("/registration")
    public String createUser(@Valid RegistrationForm form, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()){
            return REGISTRATION_PAGE;
        }


        RegistrationRequest request = modelMapper.map(form, RegistrationRequest.class);
        boolean registrationDone = userRegistrationService.createUser(request);

        if (registrationDone) {
            model.addAttribute("email", form.getEmail());
            return REGISTRATION_SUCCESS;
        } else {
//            model.addAllAttributes(response.getErrors()); //todo errors handling in registration html
            return REGISTRATION_PAGE;
        }
    }

    @GetMapping("/activate/{activationCode:.+}")
    public String activateUser(@PathVariable String activationCode) {
        boolean activated = userRegistrationService.activateUser(activationCode);
        if (activated) return "login";
        return REGISTRATION_PAGE;
    }

    @GetMapping("/resendActivationCode/{email}")
    public String resendActivationCode(@PathVariable String email, Model model) {
        boolean activationCodeResent = userRegistrationService.resendActivationCode(email);
        if (!activationCodeResent)    return REGISTRATION_PAGE;;
        model.addAttribute("email", email);
        return REGISTRATION_SUCCESS;
    }

}