package org.example.auth.controller.user.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.controller.validator.ValidEmail;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeForm implements Serializable {

    private static final long serialVersionUID = 8275421911971559913L;

    @NotBlank
    private String password;
    @ValidEmail
    private String email;
}
