package org.rimand.doc.service.document;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rimand.doc.domain.Document;
import org.rimand.doc.domain.User;
import org.rimand.doc.domain.dto.DocumentDto;
import org.rimand.doc.repo.DocumentRepo;
import org.rimand.doc.service.document.dto.DocumentCreationRequest;
import org.rimand.doc.service.storage.StorageService;
import org.rimand.doc.service.util.RandomGeneratorUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DocumentServiceTest {

    @InjectMocks
    DocumentService documentService;

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
        when(storageService.save(any(MultipartFile.class), anyString(), anyString())).thenReturn(filename);
        when(generator.generateUUID()).thenReturn(docId);
        when(documentRepo.save(any(Document.class))).thenReturn(makeMockDocument());

        DocumentDto result = documentService.create(makeMockRequest());

        assertEquals(makeMockDocumentDto(), result);
        verify(documentRepo).save(any(Document.class));
        verify(storageService).save(any(MultipartFile.class), anyString(), anyString());
    }

    @Disabled
    @Test
    void addDocument_fail_emptyRequest() {
//        Assertions.assertThrows(DocumentServiceException.class, () -> {
//            documentService.create(new DocumentCreationRequest());
//        });
        verify(documentRepo, times(0)).save(any(Document.class));
    }

    @Test
    void getAllDocuments() {
        when(documentRepo.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<DocumentDto> documents = documentService.findAll();

        assertNotNull(documents);
        verify(documentRepo).findAll();
    }

    @Test
    void getDocumentById() {
        when(documentRepo.findByDocId(anyString())).thenReturn(Optional.of(makeMockDocument()));

        Optional<DocumentDto> result = documentService.findById(anyString());

        assertEquals(Optional.of(makeMockDocumentDto()), result);
        verify(documentRepo).findByDocId(anyString());
    }


//    @Test
//    void findDocumentsByName_success() {
//        when(documentRepo.findByNameContains(anyString())).thenReturn(new ArrayList<Document>() {{
//            add(makeMockDocument());
//        }});
//
//        List<DocumentDto> documents = documentService.searchDocumentsByName(anyString());
//
//        assertEquals(1, documents.size());
//    }

//    @Test
//    void findDocumentsByName_success_emptyList() {
//        when(documentRepo.findByNameContains(anyString())).thenReturn(Collections.EMPTY_LIST);
//
//        List<DocumentDto> documents = documentService.searchDocumentsByName(anyString());
//
//        assertNotNull(documents);
//        assertEquals(0, documents.size());
//    }

//    @Test
//    void getDocumentFileById_success() {
//        when(documentRepo.findByDocId(anyString())).thenReturn(makeMockDocument());
//
//        List<DocumentDto> documents = documentService.searchDocumentsByName(anyString());
//
//        assertNotNull(documents);
//        assertEquals(0, documents.size());
//    }

    @Disabled
    @Test
    void deleteDocument_success() {
//        when(documentRepo.findByDocId(docId)).thenReturn(Optional.of(makeMockDocument()));
//        doNothing().when(documentRepo).delete(any(Document.class));
//        when(storageService.delete(filename)).thenReturn(true);
//
//        boolean deleted = documentService.delete(docId);
//
//        assertTrue(deleted);
//        verify(documentRepo).delete(any(Document.class));
//        verify(storageService).delete(filename);
    }

    private DocumentCreationRequest makeMockRequest() {
        MultipartFile file = new MockMultipartFile("name", name, null, (byte[]) null);
        User user = new User() {{
            setUsername(username);
        }};
        return new DocumentCreationRequest() {{
            setUploader(user);
            setDocumentFile(file);
        }};
    }

    private Document makeMockDocument() {
        return new Document() {{
            setFilename(filename);
            setDocName(name);
            setDocId(docId);
            setUploader(new User() {{
                setUsername(username);
            }});
        }};
    }

    private DocumentDto makeMockDocumentDto() {
        return new DocumentDto() {{
            setFilename(filename);
            setDocName(name);
            setDocId(docId);
            setUploader(username);
        }};
    }
}