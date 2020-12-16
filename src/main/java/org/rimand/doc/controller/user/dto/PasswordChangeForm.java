package org.rimand.doc.controller.user.dto;

import lombok.Data;
import org.rimand.doc.controller.validator.PasswordMatches;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@PasswordMatches
public class PasswordChangeForm {
    @NotBlank
    @Size(min = 3, max = 32, message = "password length must be between 3 and 32")
    private String oldPassword;
    @NotBlank
    @Size(min = 3, max = 32, message = "password length must be between 3 and 32")
    private String password;
    @NotBlank
    @Size(min = 3, max = 32, message = "password length must be between 3 and 32")
    private String password2;
}