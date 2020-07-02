package org.example.auth.service.document;

import java.util.List;

public interface DocumentService {
    DocumentDto addDocument(DocumentRequest request);

    List<DocumentDto> getAllDocuments();

    DocumentDto getDocumentById(String id);

    List<DocumentDto> findDocumentsByName(String name);

}


