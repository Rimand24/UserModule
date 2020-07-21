package org.example.auth.service.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {

    //@Value("${security.expiration-time.auth:86400000}")
    public long authExpirationTime;
    //@Value("${security.expiration-time.activation-code:86400000}")
    public long activationCodeExpirationTime;
    //@Value("${security.expiration-time.password-reset:86400000}")
    public long passwordResetExpirationTime;
   // @Value("${security.tokenSecret}")
    public String tokenSecret;

    @Autowired
    public TokenServiceImpl(@Value("${security.expiration-time.auth:86400000}") long authExpirationTime,
                            @Value("${security.expiration-time.activation-code:86400000}") long activationCodeExpirationTime,
                            @Value("${security.expiration-time.password-reset:86400000}") long passwordResetExpirationTime,
                            @Value("${security.tokenSecret:secret}") String tokenSecret) {
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

    @Override
    public String generatePasswordResetToken() {
        return createJWT(passwordResetExpirationTime);
    }

    @Override
    public String getEmailFromToken(String code) {
        return getStringFromToken(code);
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

    private String getStringFromToken(String code) {
        Claims claims = decodeJWT(code);
        return claims.getSubject();
    }
}
