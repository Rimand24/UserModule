package org.example.auth.service.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TokenServiceTest {

    public long authExpirationTime = 600000L;
    public long activationCodeExpirationTime = 600000L;
    public long passwordResetExpirationTime = 600000L;
    public String tokenSecret = "secret";

    private final String correctToken = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE5MDk1MzUzMTl9.nUA2DIJJ4LlS26ybujeqbpH38JimKXwe0qjRaGRE97YQPUqOmbeoLc8H3yC0odriYjU5ADmcHbqGKcipspjKbw";
    private final String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1OTQxNzU1Mjh9.PYE6BM9nP5FZDe2l9ZBo359-GMIHRlDUQHUs64cPEMpdheqBCcSCC9tBkpXnbMEylqlDFkfrj-FiJTwaTAgJvg";
    private final String incorrectToken = "120456789";

    @InjectMocks
    TokenService tokenService = new TokenService(
            authExpirationTime, activationCodeExpirationTime, passwordResetExpirationTime, tokenSecret);

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void verifyToken_success() {
        boolean correct = tokenService.verifyToken(correctToken);
        assertTrue(correct);
        Assertions.assertDoesNotThrow(() -> {tokenService.verifyToken(correctToken);});
    }

    @Test
    void verifyToken_fail_tokenExpired() {
        TokenServiceException exception = assertThrows(TokenServiceException.class, () -> {
            tokenService.verifyToken(expiredToken);
        });
        assertEquals("expired",exception.getMessage());
    }

    @Test
    void verifyToken_fail_incorrectToken() {
        TokenServiceException exception = assertThrows(TokenServiceException.class, () -> {
           tokenService.verifyToken(incorrectToken);
        });
        assertEquals("token incorrect",exception.getMessage());
    }

    @Test
    void generateEmailVerificationToken() {
    }

    @Test
    void verifyToken() {
    }

    @Test
    void generatePasswordResetToken() {
    }

    @Test
    void getEmailFromToken() {
    }
}