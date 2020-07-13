package org.example.auth.service.document;

import lombok.SneakyThrows;
import org.example.auth.domain.Document;
import org.example.auth.domain.User;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.service.storage.StorageService;
import org.example.auth.service.user.UserDto;
import org.example.auth.service.util.RandomGeneratorUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DocumentServiceImplTest {

    @InjectMocks
    DocumentServiceImpl documentService;

    @Mock
    DocumentRepo documentRepo;

    @Mock
    StorageService storageService;

    @Mock
    RandomGeneratorUtils generator;

    private static final String docId = "45f45s78g784ha545Gd51";
    private static final String filename = "45f45s78g784ha545Gd51.document.txt";
    private static final String name = "document.txt";
    private static final String username = "JohnDoe";

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @SneakyThrows
    @Test
    void addDocument_success() {
        when(storageService.save(any(byte[].class), anyString(), anyString())).thenReturn(filename);
        when(generator.generateUUID()).thenReturn(docId);
        when(documentRepo.save(any(Document.class))).thenReturn(makeMockDocument());

        DocumentDto result = documentService.addDocument(makeMockRequest());

        assertEquals(makeMockDocumentDto(), result);
        verify(documentRepo).save(any(Document.class));
        verify(storageService).save(any(byte[].class), anyString(), anyString());
    }

    @Test
    void addDocument_fail_emptyRequest() {
        Assertions.assertThrows(DocumentServiceException.class, () -> {
            documentService.addDocument(new DocumentCreationRequestDto());
        });
        verify(documentRepo, times(0)).save(any(Document.class));
    }

    @Test
    void getAllDocuments() {
        when(documentRepo.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<DocumentDto> documents = documentService.getAllDocuments();

        assertNotNull(documents);
        verify(documentRepo).findAll();
    }

    @Test
    void getDocumentById() {
        when(documentRepo.findByDocId(anyString())).thenReturn(makeMockDocument());

        DocumentDto result = documentService.getDocumentById(anyString());

        assertEquals(makeMockDocumentDto(), result);
        verify(documentRepo).findByDocId(anyString());
    }

    @Test
    void findDocumentsByName_success() {
        when(documentRepo.findByNameContains(anyString())).thenReturn(new ArrayList<Document>() {{
            add(makeMockDocument());
        }});

        List<DocumentDto> documents = documentService.searchDocumentsByName(anyString());

        assertEquals(1, documents.size());
    }

    @Test
    void findDocumentsByName_success_emptyList() {
        when(documentRepo.findByNameContains(anyString())).thenReturn(Collections.EMPTY_LIST);

        List<DocumentDto> documents = documentService.searchDocumentsByName(anyString());

        assertNotNull(documents);
        assertEquals(0, documents.size());
    }

    @Test
    void getDocumentFileById_success() {
        when(documentRepo.findByDocId(anyString())).thenReturn(makeMockDocument());

        List<DocumentDto> documents = documentService.searchDocumentsByName(anyString());

        assertNotNull(documents);
        assertEquals(0, documents.size());
    }

    @Test
    void deleteDocument_success() {
        when(documentRepo.findByDocId(anyString())).thenReturn(makeMockDocument());
        doNothing().when(documentRepo).delete(any(Document.class));
        when(storageService.delete(anyString())).thenReturn(true);

        boolean deleted = documentService.deleteDocument(anyString());

        assertTrue(deleted);
        verify(documentRepo).delete(any(Document.class));
        verify(storageService).delete(anyString());
    }

    private DocumentCreationRequestDto makeMockRequest() {
        MultipartFile file = new MockMultipartFile("name", name, null, (byte[]) null);
        User user = new User() {{
            setUsername(username);
        }};
        return new DocumentCreationRequestDto() {{
            setCreatedBy(user);
            setDocumentFile(file);
        }};
    }

    private Document makeMockDocument() {
        return new Document() {{
            setFilename(filename);
            setName(name);
            setDocId(docId);
            setCreatedBy(new User() {{
                setUsername(username);
            }});
        }};
    }

    private DocumentDto makeMockDocumentDto() {
        return new DocumentDto() {{
            setFilename(filename);
            setName(name);
            setDocId(docId);
            setCreatedBy(new UserDto() {{
                setUsername(username);
            }});
        }};
    }
}