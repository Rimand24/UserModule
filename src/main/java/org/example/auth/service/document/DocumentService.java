package org.example.auth.service.document;

import lombok.SneakyThrows;
import org.example.auth.controller.file.DocumentEditRequest;
import org.example.auth.controller.file.DocumentSearchRequest;
import org.example.auth.domain.Document;
import org.example.auth.domain.User;
import org.example.auth.domain.dto.DocumentDto;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.service.storage.StorageService;
import org.example.auth.service.util.MapperUtils;
import org.example.auth.service.util.RandomGeneratorUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final DocumentRepo documentRepo;
    private final StorageService storageService;
    private final RandomGeneratorUtils generator;
    private final MapperUtils mapper;

    public DocumentService(DocumentRepo documentRepo, StorageService storageService, RandomGeneratorUtils generator, MapperUtils mapper) {
        this.documentRepo = documentRepo;
        this.storageService = storageService;
        this.generator = generator;
        this.mapper = mapper;
    }

    public DocumentDto create(@Valid DocumentCreationRequest request) {

        MultipartFile multipartFile = request.getDocumentFile();
        String docName = StringUtils.hasText(request.getDocName()) ? request.getDocName() : multipartFile.getOriginalFilename();
        String docId = generator.generateUUID();
        String filename = storageService.save(multipartFile, docName, docId);

        Document document = new Document();
        document.setDocName(docName);
        document.setDocId(docId);
        document.setDescription(request.getDescription());
        document.setFilename(filename);
        document.setMediaType(multipartFile.getContentType());
        document.setSize(multipartFile.getSize());
        document.setUploader(request.getUploader());
        document.setUploadDateTime(LocalDateTime.now());
//        document.setPublicDocument(request.isPublicDocument());//fixme uncomment
        document.setTags(request.getTags());

        Document saved = documentRepo.save(document);

        return mapper.mapDocumentToDto(saved);
    }

    public DocumentDto edit(@Valid DocumentEditRequest request) {
        throw new RuntimeException();
    }

    public boolean delete(String docId) {
        Document document = documentRepo.findByDocId(docId).get(); //fixme opt
        documentRepo.delete(document);
        storageService.delete(document.getFilename());
        return true;
    }

    public DocumentDto findById(String docId) {
        Optional<Document> optional = documentRepo.findByDocId(docId);
        if (optional.isEmpty()) {
            throw new DocumentServiceException("document not found (id:" + docId + ")"); //fixme
        }
        return mapper.mapDocumentToDto(optional.get());
    }

    public List<DocumentDto> findByUser(String username) {
        List<Document> documents = documentRepo.findByUploader_Username(username);
        return mapper.mapDocumentListToDtoList(documents);
    }

    public List<DocumentDto> findAll() {
        List<Document> documents = documentRepo.findAll();
        return mapper.mapDocumentListToDtoList(documents);
    }

    @SneakyThrows
    public DocumentDto getDocumentFileById(String docId) {
        DocumentDto document = findById(docId);
        byte[] load = storageService.load(document.getFilename());
        document.setRawFile(load);
        return document;
    }

    public boolean changeOwnerToDeleted(String docId) {
        Document document = documentRepo.findByDocId(docId).get(); //fixme opt
        User user = new User();//fixme validation exception - not all field filled
        user.setUsername("USER_DELETED");
        document.setUploader(user);
        documentRepo.save(document);
        return true;
    }


    public List<DocumentDto> searchDocumentsByName(DocumentSearchRequest request) {
        String docName = request.getDocName();
        String username = request.getUsername();
        if (StringUtils.hasText(docName) && StringUtils.hasText(username)) {
            List<Document> documents = documentRepo.findByDocNameContainsAndUploader_UsernameContains(docName, username);
            return mapper.mapDocumentListToDtoList(documents);
        }
        if (StringUtils.hasText(docName)) {
            List<Document> documents = documentRepo.findByDocNameContains(docName);
            return mapper.mapDocumentListToDtoList(documents);
        }
        if (!StringUtils.hasText(username)) {
            List<Document> documents = documentRepo.findByUploader_UsernameContains(username);
            return mapper.mapDocumentListToDtoList(documents);
        }

        return Collections.EMPTY_LIST;
    }

}

