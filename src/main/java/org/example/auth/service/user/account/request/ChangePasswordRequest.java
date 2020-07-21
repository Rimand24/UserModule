package org.example.auth.service.user.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest implements Serializable {
    private static final long serialVersionUID = 2048403331822785746L;
    private String username;
    private String oldPassword;
    private String password;
}