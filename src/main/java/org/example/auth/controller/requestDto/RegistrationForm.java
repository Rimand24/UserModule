package org.example.auth.controller.requestDto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegistrationForm implements Serializable {

    private static final long serialVersionUID = 5447972861245361282L;
    private String username;
    private String password;
    private String password2;
    private String email;
}
