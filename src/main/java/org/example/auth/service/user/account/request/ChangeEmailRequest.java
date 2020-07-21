package org.example.auth.service.user.account.request;

import lombok.Data;
import org.example.auth.controller.validator.ValidEmail;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class ChangeEmailRequest implements Serializable {
    private static final long serialVersionUID = 6269245425803257744L;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @ValidEmail
    private String email;
}