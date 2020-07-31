package org.rimand.doc.controller.user.account;


import org.rimand.doc.domain.User;
import org.rimand.doc.service.user.account.UserAccountService;
import org.rimand.doc.service.user.account.dto.ChangeEmailRequest;
import org.rimand.doc.service.user.account.dto.ChangePasswordRequest;
import org.rimand.doc.service.user.account.dto.RegistrationRequest;
import org.rimand.doc.service.user.account.dto.UserAccountResponse;
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

    //view names
    private static final String LOGIN = "account/login";
    private static final String REGISTRATION_PAGE = "account/registration";
    private static final String REGISTRATION_SUCCESS = "account/registrationSuccess";
    private static final String MESSAGE_SENT = "account/messageSentPage";
    private static final String FORGET_PASSWORD = "account/forgetPassword";
    private static final String CHANGE_PASSWORD = "account/changePassword";
    private static final String CHANGE_EMAIL = "account/changeEmail";
    private static final String PROFILE = "account/profile";
    private static final String RESEND_ACTIVATION_CODE = "account/resendActivationCode";

    //model names
    private static final String MODEL_ERROR = "error";
    private static final String MODEL_EMAIL = "email";
    private static final String MODEL_MESSAGE = "message";

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }


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
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(form.getUsername());
        request.setEmail(form.getEmail());
        request.setPassword(form.getPassword());
        UserAccountResponse serviceResponse = userAccountService.createUser(request);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(REGISTRATION_PAGE, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(REGISTRATION_SUCCESS, MODEL_EMAIL, form.getEmail());
    }

    @GetMapping("/activate/{activationCode:.+}")
    public ModelAndView activateUser(@PathVariable String activationCode) {
        UserAccountResponse serviceResponse = userAccountService.activateUser(activationCode);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(RESEND_ACTIVATION_CODE, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(LOGIN, MESSAGE_SENT, "email activated, now you can login");
    }


    @GetMapping("/resendActivationCode")
    public ModelAndView resendActivationCodePage() {
        return new ModelAndView(RESEND_ACTIVATION_CODE);
    }

    @GetMapping("/resendActivationCode/{email}")
    public ModelAndView resendActivationCode(@PathVariable String email) {
        UserAccountResponse serviceResponse = userAccountService.resendActivationCode(email);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(REGISTRATION_PAGE, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(REGISTRATION_SUCCESS, MODEL_EMAIL, email);
    }

    @GetMapping("/forgetPassword")
    public ModelAndView showForgetPasswordForm() {
        return new ModelAndView(FORGET_PASSWORD);
    }

    @PostMapping("/forgetPassword")
    public ModelAndView forgetPassword(String username) {
        UserAccountResponse serviceResponse = userAccountService.sendResetPasswordCode(username);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(FORGET_PASSWORD, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(MESSAGE_SENT, MODEL_MESSAGE, "reset password code sent, check email");
    }

    @GetMapping("/resetPassword/{code:.+}")
    public ModelAndView resetPassword(@PathVariable String code) {
        UserAccountResponse serviceResponse = userAccountService.resetPassword(code);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(FORGET_PASSWORD, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(MESSAGE_SENT, MODEL_MESSAGE, "password changed, check email for new password"); //todo change attr
    }

    @GetMapping("/changePassword")
    public ModelAndView showChangePasswordForm() {
        return new ModelAndView(CHANGE_PASSWORD);
    }

    @PostMapping("/changePassword")
    public ModelAndView changePassword(@AuthenticationPrincipal User user, @Valid PasswordChangeForm passwordChangeForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(CHANGE_PASSWORD, MODEL_ERROR, "passwords don't match"); //fixme message to var
        }
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername(user.getUsername());
        request.setOldPassword(passwordChangeForm.getOldPassword());
        request.setPassword(passwordChangeForm.getPassword());
        UserAccountResponse serviceResponse = userAccountService.changePassword(request);

        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(CHANGE_PASSWORD, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(LOGIN, MODEL_MESSAGE, "password changed");
    }

    @GetMapping("/changeEmail")
    public ModelAndView showChangeEmailForm() {
        return new ModelAndView(CHANGE_EMAIL);
    }

    @PostMapping("/changeEmail")
    public ModelAndView changeEmailRequest(@AuthenticationPrincipal User user, @Valid EmailChangeForm form) {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setUsername(user.getUsername());
        request.setEmail(form.getEmail());
        request.setPassword(form.getPassword());
        UserAccountResponse serviceResponse = userAccountService.changeEmail(request);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(CHANGE_EMAIL, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(MESSAGE_SENT, MODEL_MESSAGE, "reset email code sent, check email");
    }

    @GetMapping("/changeEmail/{confirmCode:.+}")
    public ModelAndView changeEmailConfirm(@PathVariable String confirmCode) {
        UserAccountResponse serviceResponse = userAccountService.confirmChangeEmail(confirmCode);
        if (!serviceResponse.isSuccess()) {
            return new ModelAndView(CHANGE_EMAIL, MODEL_ERROR, serviceResponse.getStatus());
        }
        return new ModelAndView(PROFILE);
    }
}