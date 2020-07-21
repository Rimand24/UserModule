package org.example.auth.service.util;

public interface TokenService {
    String generateEmailVerificationToken();

    boolean verifyToken(String token) throws TokenServiceException;

    String generatePasswordResetToken();

    String getEmailFromToken(String code) throws TokenServiceException;
}

