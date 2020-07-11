package org.example.auth.service.util;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class RandomStringGenerator {
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public String generatePassword() {
        return generatePassword(10);
    }

    public String generatePassword(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
