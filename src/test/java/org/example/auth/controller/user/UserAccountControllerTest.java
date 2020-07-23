package org.example.auth.controller.user;

import org.example.auth.controller.user.requestDto.PasswordChangeForm;
import org.example.auth.controller.user.requestDto.RegistrationForm;
import org.example.auth.service.user.account.UserAccountService;
import org.example.auth.service.user.account.dto.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserAccountController.class)
//@AutoConfigureMockMvc
class UserAccountControllerTest {

    @MockBean
    ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService accountService;

    private static final String REGISTRATION_PAGE = "account/registration";
    private static final String REGISTRATION_SUCCESS = "account/registrationSuccess";
    private static final String MESSAGE_SENT = "account/messageSentPage";
    private static final String FORGET_PASSWORD = "account/forgetPassword";
    private static final String CHANGE_PASSWORD = "account/changePassword";
    private static final String LOGIN = "account/login";
    private static final String CHANGE_EMAIL = "account/changeEmail";
    private static final String PROFILE = "account/profile";
    private static final String RESEND_ACTIVATION_CODE = "account/resendActivationCode";

    private final String username = "Alex";
    private final String email = "example@mail.com";
    private final String password = "1234";
    private final String encryptedPassword = "4Fhd6h5gs85dS";
    private static final String TOKEN = "token.with.dots";


    @Test
    void loginPage_success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN));
    }

    @Test
    void showRegistrationPage_success() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTRATION_PAGE));
    }

    @Disabled //ModelAndView not found
    @Test
    void createUser_success() throws Exception {
        when(accountService.createUser(eq(mockRegistrationRequest())))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("email", email)
                .param("password", password)
                .param("password2", password))
                .andExpect(status().isOk())
                .andExpect(view().name(REGISTRATION_SUCCESS));

        verify(accountService).createUser(mockRegistrationRequest());
    }

    @Disabled  //Unexpected exception during isValid call.
    @Test
    public void createUser_fail_formIsNull() throws Exception {
        when(accountService.createUser(any(RegistrationRequest.class)))
                .thenReturn((new UserAccountResponse(false, UserAccountServiceErrorCode.USERNAME_NOT_FOUND)));

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(view().name(REGISTRATION_PAGE))
                .andExpect(model().attribute("error", anyString()));

        verify(accountService, times(0)).createUser(any(RegistrationRequest.class));
    }


    @Test
    public void createUser_fail_passwordsDoNotMatch() throws Exception {
        when(accountService.createUser(mockRegistrationRequest()))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.USERNAME_NOT_FOUND));

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("password", password)
                .param("password2", encryptedPassword)
                .param("email", email))
                .andExpect(view().name(REGISTRATION_PAGE));

        verify(accountService, times(0)).createUser(mockRegistrationRequest());
    }

    @Disabled  //yellow
    @Test
    public void createUser_fail_userAlreadyExists() throws Exception {
        when(accountService.createUser(mockRegistrationRequest()))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.USERNAME_ALREADY_EXISTS));

        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("password", password)
                .param("password2", encryptedPassword)
                .param("email", email))
                .andExpect(view().name(REGISTRATION_PAGE))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.USERNAME_ALREADY_EXISTS));

        verify(accountService).createUser(mockRegistrationRequest());
    }

    @Test
    void activateUser_success() throws Exception {
        when(accountService.activateUser(TOKEN))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(get("/activate/" + TOKEN))
                .andExpect(view().name(LOGIN));

        verify(accountService).activateUser(TOKEN);
    }

    @Test
    void activateUser_fail() throws Exception {
        when(accountService.activateUser(TOKEN))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED));

        mockMvc.perform(get("/activate/" + TOKEN))
                .andExpect(view().name(RESEND_ACTIVATION_CODE))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED));

        verify(accountService).activateUser(TOKEN);
    }

    @Disabled //No ModelAndView found
    @Test
    void resendActivationCodePage_success() throws Exception {
        mockMvc.perform(get("/resendActivationCode"))
                .andExpect(view().name(RESEND_ACTIVATION_CODE));
    }

    @Disabled //No ModelAndView found
    @Test
    void resendActivationCode_success() throws Exception {
        when(accountService.resendActivationCode(anyString()))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(get("/resendActivationCode/" + email))
                .andExpect(view().name(REGISTRATION_SUCCESS));

        verify(accountService).resendActivationCode(anyString());
    }

        @Disabled //No ModelAndView found
    @Test
    void resendActivationCode_fail() throws Exception {
        when(accountService.resendActivationCode(anyString()))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND));

        mockMvc.perform(get("/resendActivationCode/" + username))
                .andExpect(view().name(REGISTRATION_SUCCESS))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND));

        verify(accountService).resendActivationCode(anyString());
    }

    @Test
    void showForgetPasswordForm_success() throws Exception {
        mockMvc.perform(get("/forgetPassword"))
                .andExpect(view().name(FORGET_PASSWORD));
    }

    @Test
    void forgetPassword_success() throws Exception {
        when(accountService.sendResetPasswordCode(anyString()))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(post("/forgetPassword")
                .param("username", username))
                .andExpect(view().name(MESSAGE_SENT));

        verify(accountService).sendResetPasswordCode(anyString());
    }

    @Test
    void forgetPassword_fail() throws Exception {
        when(accountService.sendResetPasswordCode(anyString()))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.USERNAME_NOT_FOUND));

        mockMvc.perform(post("/forgetPassword")
                .param("username", username))
                .andExpect(view().name(FORGET_PASSWORD))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.USERNAME_NOT_FOUND));

        verify(accountService).sendResetPasswordCode(anyString());
    }

    @Disabled //No ModelAndView found
    @Test
    void resetPassword_success() throws Exception {
        when(accountService.sendResetPasswordCode(TOKEN))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(post("/resetPassword/" + TOKEN))
                .andExpect(view().name(MESSAGE_SENT))
                .andExpect(model().attribute("message", anyString()));

        verify(accountService).resetPassword(TOKEN);
    }

    @Disabled //No ModelAndView found
    @Test
    void resetPassword_fail() throws Exception {
        when(accountService.sendResetPasswordCode(TOKEN))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED));

        mockMvc.perform(post("/resetPassword/" + TOKEN))
                .andExpect(view().name(FORGET_PASSWORD))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.TOKEN_NOT_VERIFIED));

        verify(accountService).resetPassword(TOKEN);
    }

    @Disabled // No ModelAndView found
    @Test
    void showChangePasswordForm_success() throws Exception {
        mockMvc.perform(get("/changePassword"))
                .andExpect(view().name(CHANGE_PASSWORD));
    }

    @Disabled // No ModelAndView found
    @Test
    void changePassword_success() throws Exception {
        when(accountService.changePassword(mockChangePasswordRequest()))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(post("/changePassword")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("passwordChangeForm", mockPasswordChangeForm()))
                .andExpect(view().name(LOGIN));

        verify(accountService).changePassword(any(ChangePasswordRequest.class));
    }

    @Disabled  // No ModelAndView found
    @Test
    void changePassword_fail_incorrectOldPassword() throws Exception {
        when(accountService.changePassword(mockChangePasswordRequest()))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.PASSWORD_INCORRECT));

        mockMvc.perform(post("/changePassword")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .sessionAttr("passwordChangeForm", mockPasswordChangeForm()))
                .andExpect(view().name(CHANGE_PASSWORD))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.PASSWORD_INCORRECT));

        verify(accountService).changePassword(any(ChangePasswordRequest.class));
    }

    @Disabled // No ModelAndView found
    @Test
    void showChangeEmailForm_success() throws Exception {
        mockMvc.perform(get("/changeEmail"))
                .andExpect(view().name(CHANGE_EMAIL));
    }

    @Disabled //No ModelAndView found
    @Test
    void changeEmailRequest_success() throws Exception {
        when(accountService.changeEmail(mockEmailRequest()))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(post("/changeEmail"))
                .andExpect(view().name(MESSAGE_SENT))
                .andExpect(model().attribute("message", anyString()));

        verify(accountService).changeEmail(any(ChangeEmailRequest.class));
    }

    @Disabled //No ModelAndView found
    @Test
    void changeEmailRequest_fail() throws Exception {
        when(accountService.changeEmail(mockEmailRequest()))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND));

        mockMvc.perform(post("/changeEmail"))
                .andExpect(view().name(CHANGE_EMAIL))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND));

        verify(accountService).changeEmail(any(ChangeEmailRequest.class));
    }

    @Disabled //No ModelAndView found
    @Test
    void changeEmailConfirm_success() throws Exception {
        when(accountService.confirmChangeEmail(TOKEN))
                .thenReturn(new UserAccountResponse(true));

        mockMvc.perform(get("/changeEmail/" + TOKEN))
                .andExpect(view().name(PROFILE));

        verify(accountService).confirmChangeEmail(TOKEN);
    }

    @Disabled //No ModelAndView found
    @Test
    void changeEmailConfirm_fail() throws Exception {
        when(accountService.confirmChangeEmail(TOKEN))
                .thenReturn(new UserAccountResponse(false, UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND));

        mockMvc.perform(get("/changeEmail/" + TOKEN))
                .andExpect(view().name(CHANGE_EMAIL))
                .andExpect(model().attribute("error", UserAccountServiceErrorCode.EMAIL_TOKEN_NOT_FOUND));

        verify(accountService).confirmChangeEmail(TOKEN);
    }

    private RegistrationRequest mockRegistrationRequest() {
        return new RegistrationRequest(username, email, password);
    }

    private RegistrationForm mockRegistrationForm() {
        return new RegistrationForm(username, email, password, password);
    }

    private ChangeEmailRequest mockEmailRequest() {
        return new ChangeEmailRequest(username, email, password);
    }

    private ChangePasswordRequest mockChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword(encryptedPassword);
        request.setPassword(password);
        return request;
    }

    private PasswordChangeForm mockPasswordChangeForm() {
        PasswordChangeForm form = new PasswordChangeForm();
        form.setOldPassword(encryptedPassword);
        form.setPassword(password);
        form.setPassword2(password);
        return form;
    }
}