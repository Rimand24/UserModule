package org.example.auth.service.user.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangePasswordRequest {
    private String username;
    private String oldPassword;
    private String password;
}