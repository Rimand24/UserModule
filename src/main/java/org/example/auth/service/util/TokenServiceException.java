package org.example.auth.service.util;

public class TokenServiceException extends RuntimeException {

    public TokenServiceException() {
        super();
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
