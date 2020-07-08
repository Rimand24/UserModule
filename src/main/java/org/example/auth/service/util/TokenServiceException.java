package org.example.auth.service.util;

import io.jsonwebtoken.ExpiredJwtException;

public class TokenServiceException extends RuntimeException {
    public TokenServiceException() {
    }

    public TokenServiceException(String message) {
        super(message);
    }

    public TokenServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenServiceException(Throwable cause) {
        super(cause);
    }
}
