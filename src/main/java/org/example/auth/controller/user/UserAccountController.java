package org.example.auth.controller.user;

import org.example.auth.controller.user.requestDto.PasswordChangeForm;
import org.example.auth.controller.user.requestDto.RegistrationForm;
import org.example.auth.domain.User;
import org.example.auth.service.user.account.request.ChangePasswordRequest;
import org.example.auth.service.user.account.request.RegistrationRequest;
import org.example.auth.service.user.account.UserAccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserAccountController {

    private static final String REGISTRATION_PAGE = "registration";
    private static final String REGISTRATION_SUCCESS = "registrationSuccess";
    private static final String MESSAGE_SENT = "messageSentPage";

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserAccountService userAccountService;

//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto() {
//        return new UserRegistrationDto();
//    }
//
//    @GetMapping
//    public String showRegistrationForm(Model model) {
//        return "registration";
//    }
//
//    @PostMapping
//    public String registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto, BindingResult result) {
//
//        User existing = userService.findByEmail(userDto.getEmail());
//        if (existing != null) {
//            result.rejectValue("email", null, "There is already an account registered with that email");
//        }
//        if (result.hasErrors())             return "registration";
//        userService.save(userDto);
//        return "redirect:/registration?success";
//    }

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
        boolean registrationDone = userAccountService.createUser(request);

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
        boolean activated = userAccountService.activateUser(activationCode);
        if (activated) return "login";
        return REGISTRATION_PAGE;
    }

    @GetMapping("/resendActivationCode/{email}")
    public String resendActivationCode(@PathVariable String email, Model model) {
        boolean activationCodeResent = userAccountService.resendActivationCode(email);
        if (!activationCodeResent)    return REGISTRATION_PAGE;;
        model.addAttribute("email", email);
        return REGISTRATION_SUCCESS;
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
        boolean changed = userAccountService.changePassword(request);
        if (changed) return "login";
        return "redirect:/changePassword";
    }

    @GetMapping("/forgetPassword")
    public String getForgetPassword() {
        return "forgetPassword";
    }

    @PostMapping("/forgetPassword")
    public String postForgetPassword(String username, Model model) {
        userAccountService.sendResetPasswordCode(username);
        model.addAttribute("message", "reset password code sent, check email");
        return MESSAGE_SENT;
    }

    @GetMapping("/resetPassword/{code}")
    public String resetPassword(@PathVariable String code, Model model) {
        userAccountService.resetPassword(code);

        model.addAttribute("message", "password changed, check email");
        return MESSAGE_SENT;
    }
}