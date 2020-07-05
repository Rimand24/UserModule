package org.example.auth.service.util;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UUIDGenerator {
    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
