package org.example.auth.service.document;

import java.util.List;

public interface DocumentService {
    DocumentDto addDocument(DocumentCreationRequestDto request);

    List<DocumentDto> getAllDocuments();

    DocumentDto getDocumentById(String id);

    List<DocumentDto> findDocumentsByName(String name);

    boolean deleteDocument(String id);

    DocumentDto getDocumentFileById(String id);

}

