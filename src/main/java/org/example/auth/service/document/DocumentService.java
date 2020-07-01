package org.example.auth.service.document;

import org.example.auth.controller.requestDto.DocumentForm;

import java.util.List;

public interface DocumentService {
    List<DocumentDto> getAllDocuments();

    DocumentDto getDocument(String id);

    DocumentDto addDocument(DocumentForm form);
}


