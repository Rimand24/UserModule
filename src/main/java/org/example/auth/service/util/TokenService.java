package org.example.auth.service.util;

public interface TokenService {
    String generateEmailVerificationToken(String username);

    boolean verifyToken(String token);
}
