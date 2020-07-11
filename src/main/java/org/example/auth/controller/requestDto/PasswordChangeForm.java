package org.example.auth.controller.requestDto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PasswordChangeForm implements Serializable {
    private static final long serialVersionUID = -8565974236159842106L;
    private String oldPassword;
    private String password;
    private String password2;
}

