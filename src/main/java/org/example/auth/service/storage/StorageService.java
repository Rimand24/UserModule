package org.example.auth.service.storage;

import java.io.IOException;

public interface StorageService {
    byte[] load(String filename) throws IOException;

    boolean delete(String filename);

    String save(byte[] bytes, String name, String docId) throws IOException;
}

