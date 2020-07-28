package org.example.auth.service.document;

import lombok.Data;
import org.example.auth.domain.Tag;
import org.example.auth.domain.User;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class DocumentCreationRequest {
    private String docName;
    @Size(max = 1024)
    private String description;
    private Set<Tag> tags;
    @NotNull
    private User uploader;
    private boolean publicDocument;
    @NotNull
    private MultipartFile documentFile;
}
