package org.example.auth.controller.file;

import lombok.Data;

@Data
public class DocumentSearchRequest {
    private String docName;
    private String username;
}
