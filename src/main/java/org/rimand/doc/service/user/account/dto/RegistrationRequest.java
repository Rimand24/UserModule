package org.rimand.doc.service.user.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;

}
