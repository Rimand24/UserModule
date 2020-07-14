package org.example.auth.controller.requestDto;

import lombok.Data;
import org.example.auth.controller.validator.PasswordMatches;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@PasswordMatches
public class PasswordChangeForm implements Serializable {
    private static final long serialVersionUID = -8565974236159842106L;
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