package org.example.auth.service.user.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class RegistrationRequest implements Serializable {
    private static final long serialVersionUID = -1064645942032271857L;
    private String username;
    private String email;
    private String password;

}
