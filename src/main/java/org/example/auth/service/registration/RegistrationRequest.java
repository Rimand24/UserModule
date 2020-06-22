package org.example.auth.service.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.auth.domain.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest implements Serializable {

    private static final long serialVersionUID = 2331825251467749095L;
    private String username;
    private String password;
    private String email;

}
