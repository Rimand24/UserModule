package org.rimand.doc.controller.user.dto;

import lombok.Data;
import org.rimand.doc.controller.validator.ValidEmail;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EmailChangeForm {
    @NotBlank
    private String password;
    @ValidEmail
    private String email;
}
