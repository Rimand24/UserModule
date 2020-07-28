package org.example.auth.service.user.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.controller.validator.ValidEmail;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class ChangeEmailRequest {
    @NotBlank
    private String username;
    @ValidEmail
    private String email;
    @NotBlank
    private String password;

}