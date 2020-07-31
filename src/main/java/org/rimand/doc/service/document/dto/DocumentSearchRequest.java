package org.rimand.doc.service.document.dto;

import lombok.Data;

@Data
public class DocumentSearchRequest {
    private String docName;
    private String username;
}
