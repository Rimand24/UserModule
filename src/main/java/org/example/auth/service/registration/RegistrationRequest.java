package org.example.auth.service.registration;

import lombok.Data;
import org.example.auth.domain.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
public class RegistrationRequest implements Serializable {

    private static final long serialVersionUID = 2331825251467749095L;
    private String username;
    private String password;
    private String email;

}
