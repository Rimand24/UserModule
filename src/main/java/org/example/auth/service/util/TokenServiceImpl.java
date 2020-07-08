package org.example.auth.service.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    public final long authExpirationTime;
    public final long activationCodeExpirationTime;
    public final long passwordResetExpirationTime;
    public final String tokenSecret;

    @Autowired
    public TokenServiceImpl(@Value("${security.expiration-time.auth}") long authExpirationTime,
                            @Value("${security.expiration-time.activation-code}") long activationCodeExpirationTime,
                            @Value("${security.expiration-time.password-reset}") long passwordResetExpirationTime,
                            @Value("${security.tokenSecret}") String tokenSecret) {
        this.authExpirationTime = authExpirationTime;
        this.activationCodeExpirationTime = activationCodeExpirationTime;
        this.passwordResetExpirationTime = passwordResetExpirationTime;
        this.tokenSecret = tokenSecret;
    }

    @Override
    public String generateEmailVerificationToken() {
        return createJWT(activationCodeExpirationTime);
    }

    @Override
    public boolean verifyToken(String token) {
        decodeJWT(token); //throws TokenServiceException if token incorrect
        return true;
    }

    private String createJWT(long expirationTime) {
        String token = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                //.signWith(SignatureAlgorithm.HS512, DatatypeConverter.parseBase64Binary(tokenSecret))
                .compact();
        System.out.println("generated token: " + token); //fixme debug
        return token;
    }

    private Claims decodeJWT(String jwt) {
        try {
            return Jwts.parser()
                    //.setSigningKey(DatatypeConverter.parseBase64Binary(tokenSecret))
                    .setSigningKey(tokenSecret)
                    .parseClaimsJws(jwt).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenServiceException("expired", e);
        } catch (JwtException e) {
            throw new TokenServiceException("token incorrect", e);
        }
    }
}
