package org.rimand.doc.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_SUPER_ADMIN, ROLE_ADMIN_ACCOUNT, ROLE_ADMIN_DOCUMENT, ROLE_USER;


    @Override
    public String getAuthority() {
        return name();
    }
}
