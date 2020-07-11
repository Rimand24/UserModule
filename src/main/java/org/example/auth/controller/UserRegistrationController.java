package org.example.auth.controller;

import org.example.auth.controller.requestDto.PasswordChangeForm;
import org.example.auth.controller.requestDto.RegistrationForm;
import org.example.auth.domain.User;
import org.example.auth.service.registration.RegistrationRequest;
import org.example.auth.service.registration.UserRegistrationService;
import org.example.auth.service.user.ChangePasswordRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            model.addAttribute("email", form.getEmail());
            return "registrationSuccess";
        } else {
            //model.addAllAttributes(response.getErrors()); //todo errors handling in registration html
            return "registration";
        }
    }

    @GetMapping("/activate/{activationCode}")
    public String activateUser(@PathVariable String activationCode) {
        boolean activated = userRegistrationService.activateUser(activationCode);
        if (activated) return "login";
        return "registration";
    }

    @GetMapping("/resendActivationCode/{email}")
    public String resendActivationCode(@PathVariable String email, Model model) {
        boolean activationCodeResent = userRegistrationService.resendActivationCode(email);
        if(!activationCodeResent) return "registration";
        model.addAttribute("email", email);
        model.addAttribute("resend", true);
        return "registrationSuccess";
    }

    @GetMapping("/changePassword")
    public String getChangePassword(Model model) {
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String postChangePassword(@AuthenticationPrincipal User user, PasswordChangeForm passwordChangeForm, Model model) {
        if (!passwordChangeForm.getPassword().equals(passwordChangeForm.getPassword2())) {
            System.out.println("pass does not match");//fixme
            return "redirect:/changePassword";
        }

        ChangePasswordRequest request = new ChangePasswordRequest(user.getUsername(), passwordChangeForm.getOldPassword(), passwordChangeForm.getPassword());
        userRegistrationService.changePassword(request);
        return "login";
    }

    @GetMapping("/forgetPassword")
    public String getForgetPassword() {
        return "forgetPassword";
    }

    @PostMapping("/forgetPassword")
    public String postForgetPassword(String username, Model model) {
        userRegistrationService.sendResetPasswordCode(username);
        model.addAttribute("message", "reset password code sent, check email");
        return "messageSentPage";
    }

    @GetMapping("/resetPassword/{code}")
    public String resetPassword(@PathVariable String code, Model model) {
        userRegistrationService.resetPassword(code);

        model.addAttribute("message", "password changed, check email");
        return "messageSentPage";
    }

}