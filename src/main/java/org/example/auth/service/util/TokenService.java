package org.example.auth.service.util;

public interface TokenService {
    String generateEmailVerificationToken();

    boolean verifyToken(String token);
}
