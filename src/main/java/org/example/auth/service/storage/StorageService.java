package org.example.auth.service.storage;

import java.io.IOException;

public interface StorageService {
    boolean save(byte[] file, String filename) throws IOException;

    byte[] load(String filename) throws IOException;

    boolean delete(String filename);
}

