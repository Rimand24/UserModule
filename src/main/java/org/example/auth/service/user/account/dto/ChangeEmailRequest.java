package org.example.auth.service.user.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.controller.validator.ValidEmail;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailRequest implements Serializable {
    private static final long serialVersionUID = 6269245425803257744L;
    @NotBlank
    private String username;
    @ValidEmail
    private String email;
    @NotBlank
    private String password;

}