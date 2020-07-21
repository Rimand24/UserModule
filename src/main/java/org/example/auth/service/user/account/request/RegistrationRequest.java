package org.example.auth.service.user.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest implements Serializable {
    private static final long serialVersionUID = -1064645942032271857L;
    private String username;
    private String password;
    private String email;
}
