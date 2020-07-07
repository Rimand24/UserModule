package org.example.auth.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalSystemStorageService implements StorageService{

    private final String uploadPath;

    public LocalSystemStorageService(@Value("${upload.path}") String uploadPath) {
        uploadPath = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        this.uploadPath = uploadPath;
    }

    @Override
    public boolean save(byte[] file, String filename) throws IOException {
        Path path = Path.of(uploadPath + filename);
        Files.write(path, file);
        return true;
    }

    @Override
    public byte[] load(String filename) throws IOException {
        Path path = Path.of(uploadPath + filename);

        if (path.toFile().exists() && path.toFile().isFile()) {
            return Files.readAllBytes(path);
        }
        throw new StorageServiceException("file does not exist");
    }

    @Override
    public boolean delete(String filename) {
        File file = new File(uploadPath + filename);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

}
