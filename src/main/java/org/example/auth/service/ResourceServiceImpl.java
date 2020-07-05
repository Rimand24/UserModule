package org.example.auth.service;

import jdk.jshell.spi.ExecutionControl;
import lombok.SneakyThrows;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Value("${upload.path}")
    private String uploadPath;

    @SneakyThrows
    @Override
    public boolean saveFile(MultipartFile multipartFile, String filename) {
        uploadPath = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";

        Path absolutePath = Path.of(uploadPath + filename);
        multipartFile.transferTo(absolutePath);
        return true;
    }

    @Override
    public Path getFile(String filename) {
        throw new NotYetImplementedException(this.getClass().getName().toString() + ": getFile method");
    }

    @Override
    public boolean deleteFile(String filename) {
        File file = new File(uploadPath + filename);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
}
