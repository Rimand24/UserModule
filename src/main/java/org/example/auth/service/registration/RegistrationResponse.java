package org.example.auth.service.registration;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class RegistrationResponse implements Serializable {
    private static final long serialVersionUID = -6014419023428086133L;
    private boolean success;
    private Set<String> errors;

    public void addError(String s) {
        errors.add(s);
    }
}
