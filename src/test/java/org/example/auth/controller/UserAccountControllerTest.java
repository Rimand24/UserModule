package org.example.auth.controller;

import org.example.auth.service.user.account.request.RegistrationRequest;
import org.example.auth.service.user.account.UserAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAccountService registrationService;

    private final String username = "Alex";
    private final String email = "example@mail.com";
    private final String password = "1234";
    private final String encryptedPassword = "4Fhd6h5gs85dS";
    private final String registrationPageName = "registration";
    private final String registrationSuccessPageName = "registrationSuccess";

    @Test
    public void getRegistrationTest() throws Exception {
        assertEquals(registrationPageName, Objects.requireNonNull(
                mockMvc
                        .perform(get("/registration"))
                        .andExpect(status().isOk())
                        .andReturn().getModelAndView()).getViewName());
    }

    @Test
    public void postRegistrationTest_success() throws Exception {

        when(registrationService.createUser(eq(new RegistrationRequest() {
            {
                setUsername(username);
                setPassword(password);
                setEmail(email);
            }
        })))
                .thenReturn(true);

        assertEquals(registrationSuccessPageName, Objects.requireNonNull(
                mockMvc.perform(
                        post("/registration")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .param("password2", password)
                                .param("email", email)
                )
                        .andExpect(status().isOk())
                        .andReturn().getModelAndView()).getViewName());

        verify(registrationService).createUser(ArgumentMatchers.any(RegistrationRequest.class));
    }

    @Test
    public void postRegistrationTest_fail_formIsNull() throws Exception {
//todo  check validation - RegistrationForm must not be incorrect (null/empty/only spaces/incorect symbols)
        when(registrationService.createUser(any(RegistrationRequest.class))).thenReturn(false);

        assertEquals("fields does not filled",
                mockMvc.perform(
                        post("/registration")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", "")
                                .param("password", "")
                                .param("password2", "")
                                .param("email", "")
                ).andReturn().getModelAndView().getModel().get("error").toString()
        );

        verify(registrationService, times(0)).createUser(any(RegistrationRequest.class));
    }

    @Test
    public void postRegistrationTest_fail_passwordsDoNotMatch() throws Exception {
        when(registrationService.createUser(any(RegistrationRequest.class))).thenReturn(false);

        assertEquals("passwords does not match",
                mockMvc.perform(
                        post("/registration")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", username)
                                .param("password", password)
                                .param("password2", encryptedPassword)
                                .param("email", email)
                ).andReturn().getModelAndView().getModel().get("error").toString()
        );

        verify(registrationService, times(0)).createUser(any(RegistrationRequest.class));
    }
}

