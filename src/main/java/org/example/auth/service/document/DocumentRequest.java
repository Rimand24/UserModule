package org.example.auth.service.document;

import lombok.Data;
import org.example.auth.domain.User;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentRequest {

    private MultipartFile documentFile;

    private User createdBy;


}
