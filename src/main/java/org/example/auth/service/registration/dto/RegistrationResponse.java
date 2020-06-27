package org.example.auth.service.registration.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class RegistrationResponse implements Serializable {
    private static final long serialVersionUID = -6014419023428086133L;
    private boolean success = false;
    private Set<String> errors = new HashSet<>();

    public void addError(String s) {
        errors.add(s);
    }
}
