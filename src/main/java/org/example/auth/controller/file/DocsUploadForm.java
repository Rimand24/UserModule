package org.example.auth.controller.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DocsUploadForm {
    String name;
    List<String> tagList;
    @NotNull
    MultipartFile file;
}
