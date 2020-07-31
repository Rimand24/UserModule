package org.rimand.doc.controller.file.dto;

import lombok.Data;
import org.rimand.doc.domain.Tag;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class DocumentUploadForm {
    private String docName;
    @Size(max = 1024)
    private String description;
    private Set<Tag> tags;
    private boolean publicDocument;
    @NotNull
    private MultipartFile file;
}
