package org.example.auth.controller;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegistrationForm implements Serializable {

    private static final long serialVersionUID = 7777706523597109309L;
    private String username;
    private String password;
    private String password2;
    private String email;
}
