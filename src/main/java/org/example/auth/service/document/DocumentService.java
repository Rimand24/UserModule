package org.example.auth.service.document;

import java.util.List;

public interface DocumentService {
    DocumentDto addDocument(DocumentCreationRequestDto request);

    List<DocumentDto> getAllDocuments();

    DocumentDto getDocumentById(String id);

    List<DocumentDto> searchDocumentsByName(String name);

    boolean deleteDocument(String id);

    List<DocumentDto> findDocumentsByUser(String username);

    DocumentDto getDocumentFileById(String id);

    boolean changeOwnerToDeleted(String docId);
}

