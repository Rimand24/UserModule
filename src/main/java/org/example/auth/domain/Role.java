package org.example.auth.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, USER, DOC_REDACTOR;
//            ROLE_ADMIN, ROLE_USER, ROLE_DOC_REDACTOR;
    @Override
    public String getAuthority() {
        return name();
    }
}