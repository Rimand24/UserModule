package org.example.auth.controller.user;

import org.example.auth.controller.user.requestDto.EmailChangeForm;
import org.example.auth.controller.user.requestDto.PasswordChangeForm;
import org.example.auth.controller.user.requestDto.RegistrationForm;
import org.example.auth.domain.User;
import org.example.auth.service.user.account.dto.ChangeEmailRequest;
import org.example.auth.service.user.account.dto.ChangePasswordRequest;
import org.example.auth.service.user.account.dto.RegistrationRequest;
import org.example.auth.service.user.account.UserAccountService;
import org.example.auth.service.user.account.dto.UserAccountResponse;
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

    private static final String REGISTRATION_PAGE = "account/registration";
    private static final String REGISTRATION_SUCCESS = "account/registrationSuccess";
    private static final String MESSAGE_SENT = "account/messageSentPage";
    private static final String FORGET_PASSWORD = "account/forgetPassword";
    private static final String CHANGE_PASSWORD = "account/changePassword";
    private static final String LOGIN = "account/login";
    private static final String CHANGE_EMAIL = "account/changeEmail";
    private static final String PROFILE = "account/profile";

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserAccountService userAccountService;

    @GetMapping("/login")
    public String login() {
        return LOGIN;
    }

    @GetMapping("/registration")
    public String showRegistrationPage() {
        return REGISTRATION_PAGE;
    }

    @PostMapping("/registration")
    public String createUser(@Valid RegistrationForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) { //fixme not tested
            return REGISTRATION_PAGE;
        }
        RegistrationRequest request = new RegistrationRequest(form.getUsername(), form.getPassword(), form.getEmail());
        UserAccountResponse serviceResponse = userAccountService.createUser(request);
        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return REGISTRATION_PAGE;
        }
        model.addAttribute("email", form.getEmail());
        return REGISTRATION_SUCCESS;
    }

    @GetMapping("/activate/{activationCode:.+}")
    public String activateUser(@PathVariable String activationCode, Model model) {
        UserAccountResponse serviceResponse = userAccountService.activateUser(activationCode);
        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return REGISTRATION_PAGE;
        }
        return LOGIN;
    }

    @GetMapping("/resendActivationCode/{email}")
    public String resendActivationCode(@PathVariable String email, Model model) {
        UserAccountResponse serviceResponse = userAccountService.resendActivationCode(email);
        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return REGISTRATION_PAGE;
        }
        model.addAttribute("email", email);
        return REGISTRATION_SUCCESS;
    }

    @GetMapping("/forgetPassword")
    public String showForgetPasswordForm() {
        return FORGET_PASSWORD;
    }

    @PostMapping("/forgetPassword")
    public String forgetPassword(String username, Model model) {
        UserAccountResponse serviceResponse = userAccountService.sendResetPasswordCode(username);
        if (!serviceResponse.isSuccess()) {
            //todo add some error - dont work with redirect?
            //model.addAttribute("error", serviceResponse.getError());
            return "redirect:/forgetPassword";
        }
        model.addAttribute("message", "reset password code sent, check email"); //todo change attr
        return MESSAGE_SENT;
    }

    @GetMapping("/resetPassword/{code:.+}")
    public String resetPassword(@PathVariable String code, Model model) {
        UserAccountResponse serviceResponse = userAccountService.resetPassword(code);
        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return FORGET_PASSWORD;
        }
        model.addAttribute("message", "password changed, check email for new password"); //todo change attr
        return MESSAGE_SENT;
    }

    @GetMapping("/changePassword")
    public String showChangePasswordForm() {
        return CHANGE_PASSWORD;
    }

    @PostMapping("/changePassword")
    public String changePassword(@AuthenticationPrincipal User user, @Valid PasswordChangeForm passwordChangeForm, Model model) {
//        if (!passwordChangeForm.getPassword().equals(passwordChangeForm.getPassword2())) {
//            System.out.println("pass does not match");//fixme use bindingf results instead
//            return "redirect:/changePassword";
//        }
        ChangePasswordRequest request = new ChangePasswordRequest(user.getUsername(), passwordChangeForm.getOldPassword(), passwordChangeForm.getPassword());
        UserAccountResponse serviceResponse = userAccountService.changePassword(request);

        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return CHANGE_PASSWORD;
        }

        model.addAttribute("status", "password changed");//todo view does not use attr atm
        return LOGIN;
    }

    @GetMapping("/changeEmail")
    public String showChangeEmailForm() {
        return CHANGE_EMAIL;
    }

    @PostMapping("/changeEmail")
    public String changeEmailRequest(@AuthenticationPrincipal User user, @Valid EmailChangeForm emailChangeForm, Model model) {
        ChangeEmailRequest request = new ChangeEmailRequest(user.getUsername(), emailChangeForm.getPassword(), emailChangeForm.getEmail());
        UserAccountResponse serviceResponse = userAccountService.changeEmail(request);
        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return CHANGE_EMAIL;
        }
        model.addAttribute("message", "reset email code sent, check email");
        return MESSAGE_SENT;
    }

    @GetMapping("/changeEmail/{confirmCode:.+}")
    public String changeEmailConfirm(@PathVariable String confirmCode, Model model) {
        UserAccountResponse serviceResponse = userAccountService.confirmChangeEmail(confirmCode);
        if (!serviceResponse.isSuccess()) {
            model.addAttribute("error", serviceResponse.getError());
            return CHANGE_EMAIL;
        }
        return PROFILE;
    }
}