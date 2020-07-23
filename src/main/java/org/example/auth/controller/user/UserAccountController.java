package org.example.auth.controller.user;

import org.example.auth.controller.user.requestDto.EmailChangeForm;
import org.example.auth.controller.user.requestDto.PasswordChangeForm;
import org.example.auth.controller.user.requestDto.RegistrationForm;
import org.example.auth.domain.User;
import org.example.auth.service.user.account.UserAccountService;
import org.example.auth.service.user.account.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class UserAccountController {

    private static final String LOGIN = "account/login";
    private static final String REGISTRATION_PAGE = "account/registration";
    private static final String REGISTRATION_SUCCESS = "account/registrationSuccess";
    private static final String MESSAGE_SENT = "account/messageSentPage";
    private static final String FORGET_PASSWORD = "account/forgetPassword";
    private static final String CHANGE_PASSWORD = "account/changePassword";
    private static final String CHANGE_EMAIL = "account/changeEmail";
    private static final String PROFILE = "account/profile";
    private static final String RESEND_ACTIVATION_CODE = "account/resendActivationCode";

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserAccountService userAccountService;

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN;
    }

    @GetMapping("/registration")
    public String showRegistrationPage() {
        return REGISTRATION_PAGE;
    }

    @PostMapping("/registration")
    public ModelAndView createUser(@Valid RegistrationForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { //fixme not tested
            return new ModelAndView(REGISTRATION_PAGE);
        }
        RegistrationRequest request = new RegistrationRequest(form.getUsername(), form.getPassword(), form.getEmail());
        UserAccountResponse serviceResponse = userAccountService.createUser(request);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView (REGISTRATION_PAGE,"error", serviceResponse.getStatus());
        }
        return new ModelAndView(REGISTRATION_SUCCESS, "email", form.getEmail());
    }

    @GetMapping("/activate/{activationCode:.+}")
    public ModelAndView activateUser(@PathVariable String activationCode) {
        UserAccountResponse serviceResponse = userAccountService.activateUser(activationCode);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(RESEND_ACTIVATION_CODE,"error", serviceResponse.getStatus());
        }
        return new ModelAndView(LOGIN, "message", "email activated, now you can login");
    }


    @GetMapping("/resendActivationCode")
    public ModelAndView resendActivationCodePage() {
        return new ModelAndView(RESEND_ACTIVATION_CODE);
    }

    @GetMapping("/resendActivationCode/{email}")
    public ModelAndView resendActivationCode(@PathVariable String email) {
        UserAccountResponse serviceResponse = userAccountService.resendActivationCode(email);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(REGISTRATION_PAGE,"error", serviceResponse.getStatus());
        }
        return new ModelAndView(REGISTRATION_SUCCESS,"email", email);
    }

    @GetMapping("/forgetPassword")
    public ModelAndView showForgetPasswordForm() {
        return new ModelAndView(FORGET_PASSWORD);
    }

    @PostMapping("/forgetPassword")
    public ModelAndView forgetPassword(String username) {
        UserAccountResponse serviceResponse = userAccountService.sendResetPasswordCode(username);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView (FORGET_PASSWORD, "error", serviceResponse.getStatus());
        }
        return new ModelAndView (MESSAGE_SENT,"message", "reset password code sent, check email");
    }

    @GetMapping("/resetPassword/{code:.+}")
    public ModelAndView resetPassword(@PathVariable String code) {
        UserAccountResponse serviceResponse = userAccountService.resetPassword(code);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(FORGET_PASSWORD, "error", serviceResponse.getStatus());
        }
        return new ModelAndView(MESSAGE_SENT,"message", "password changed, check email for new password"); //todo change attr
    }

    @GetMapping("/changePassword")
    public ModelAndView showChangePasswordForm() {
        return new ModelAndView(CHANGE_PASSWORD);
    }

    @PostMapping("/changePassword")
    public ModelAndView changePassword(@AuthenticationPrincipal User user, @Valid PasswordChangeForm passwordChangeForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(CHANGE_PASSWORD, "error", "passwords don't match"); //fixme message to var
        }
        ChangePasswordRequest request = new ChangePasswordRequest(user.getUsername(), passwordChangeForm.getOldPassword(), passwordChangeForm.getPassword());
        UserAccountResponse serviceResponse = userAccountService.changePassword(request);

        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(CHANGE_PASSWORD,"error", serviceResponse.getStatus());
        }
        return new ModelAndView(LOGIN, "message", "password changed");
    }

    @GetMapping("/changeEmail")
    public ModelAndView showChangeEmailForm() {
        return new ModelAndView(CHANGE_EMAIL);
    }

    @PostMapping("/changeEmail")
    public ModelAndView changeEmailRequest(@AuthenticationPrincipal User user, @Valid EmailChangeForm emailChangeForm) {
        ChangeEmailRequest request = new ChangeEmailRequest(user.getUsername(),emailChangeForm.getEmail(), emailChangeForm.getPassword());
        UserAccountResponse serviceResponse = userAccountService.changeEmail(request);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(CHANGE_EMAIL,"error", serviceResponse.getStatus());
        }
        return new ModelAndView(MESSAGE_SENT,"message", "reset email code sent, check email");
    }

    @GetMapping("/changeEmail/{confirmCode:.+}")
    public ModelAndView changeEmailConfirm(@PathVariable String confirmCode) {
        UserAccountResponse serviceResponse = userAccountService.confirmChangeEmail(confirmCode);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(CHANGE_EMAIL, "error", serviceResponse.getStatus());
        }
        return new ModelAndView(PROFILE);
    }
}