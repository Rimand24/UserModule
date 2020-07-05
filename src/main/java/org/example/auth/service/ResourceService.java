package org.example.auth.service;

import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface ResourceService {

    boolean saveFile(MultipartFile multipartFile, String filename);

    Path getFile(String filename);

    boolean deleteFile(String filename);
}
