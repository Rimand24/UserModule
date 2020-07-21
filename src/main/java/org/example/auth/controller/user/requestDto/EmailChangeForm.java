package org.example.auth.controller.user.requestDto;

import lombok.Data;
import org.example.auth.controller.validator.ValidEmail;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmailChangeForm implements Serializable {

    private static final long serialVersionUID = 8275421911971559913L;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @ValidEmail
    private String email;
}
