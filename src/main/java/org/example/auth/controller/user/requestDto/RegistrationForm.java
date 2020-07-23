package org.example.auth.controller.user.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.auth.controller.validator.PasswordMatches;
import org.example.auth.controller.validator.ValidEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class RegistrationForm {
    @NotBlank
    @Size(min = 3, max = 32, message = "username length must be between 3 and 32")
    private String username;
    @NotBlank
    @ValidEmail
    private String email;
    @NotBlank
    @Size(min = 3, max = 32, message = "password length must be between 3 and 32")
    private String password;
    @NotBlank
    @Size(min = 3, max = 32, message = "password length must be between 3 and 32")
    private String password2;
}