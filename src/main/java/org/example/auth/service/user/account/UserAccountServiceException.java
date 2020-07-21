package org.example.auth.service.user.account;

public class UserAccountServiceException extends RuntimeException {
    public UserAccountServiceException() {
        super();
    }

    public UserAccountServiceException(String message) {
        super(message);
    }

    public UserAccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAccountServiceException(Throwable cause) {
        super(cause);
    }

}
