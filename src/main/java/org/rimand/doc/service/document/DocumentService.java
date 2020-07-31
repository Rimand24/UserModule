package org.rimand.doc.service.document;

import lombok.SneakyThrows;
import org.rimand.doc.domain.Document;
import org.rimand.doc.domain.Role;
import org.rimand.doc.domain.User;
import org.rimand.doc.domain.dto.DocumentDto;
import org.rimand.doc.repo.DocumentRepo;
import org.rimand.doc.service.document.dto.DocumentCreationRequest;
import org.rimand.doc.service.document.dto.DocumentEditRequest;
import org.rimand.doc.service.document.dto.DocumentSearchRequest;
import org.rimand.doc.service.storage.StorageService;
import org.rimand.doc.service.util.MapperUtils;
import org.rimand.doc.service.util.RandomGeneratorUtils;
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
        document.setPublicDocument(request.isPublicDocument());
        document.setTags(request.getTags());

        Document saved = documentRepo.save(document);

        return mapper.mapDocumentToDto(saved);
    }

    //fixme
    public Optional<DocumentDto> edit(@Valid DocumentEditRequest request) {
        Optional<Document> optional = documentRepo.findByDocId(request.getDocId());
        if (optional.isEmpty()) {
//            throw new Exception("doc not found"); //fixme
            return Optional.empty();
        }
        Document document = optional.get();
        if (StringUtils.hasText(request.getDocName())) {
            document.setDocName(request.getDocName());
        }
        document.setPublicDocument(request.isPublicDocument());
        if (!request.getTags().isEmpty()) {
            document.setTags(request.getTags());
        }
        document.setLastEditDateTime(LocalDateTime.now());
        Document save = documentRepo.save(document);
        return Optional.of(mapper.mapDocumentToDto(save));

    }

    public boolean delete(String docId, User user) {
        Optional<Document> optional = documentRepo.findByDocId(docId);
        if (optional.isEmpty()) {
//            throw new Exception("doc not found"); //fixme
            return false;
        }
        Document document = optional.get();
        if (document.getUploader().getUsername().equals(user.getUsername())
                || user.getRoles().contains(Role.ROLE_ADMIN_DOCUMENT)) { //todo fixme check if admin works with role hierarchy
            documentRepo.delete(optional.get());
            storageService.delete(optional.get().getFilename());
            return true;
        }
        return false;
    }

    public Optional<DocumentDto> findById(String docId) {
        Optional<Document> optional = documentRepo.findByDocId(docId);
        if (optional.isEmpty()) {
            //   throw new DocumentServiceException("document not found (id:" + docId + ")"); //fixme
            return Optional.empty();
        }
        return Optional.of(mapper.mapDocumentToDto(optional.get()));
    }

    public List<DocumentDto> findAllByUser(String username) {
        List<Document> documents = documentRepo.findByUploader_Username(username);
        return mapper.mapDocumentListToDtoList(documents);
    }

    public List<DocumentDto> findAll() {
        List<Document> documents = documentRepo.findAll();
        return mapper.mapDocumentListToDtoList(documents);
    }

    @SneakyThrows
    public Optional<DocumentDto> downloadDocById(String docId) {
        Optional<DocumentDto> optional = findById(docId);
        if (optional.isPresent()) {
            byte[] load = storageService.load(optional.get().getFilename());
            optional.get().setRawFile(load);
        }
        return optional;
    }

    @Deprecated  //fixme may cause errors
    public boolean changeOwnerToDeleted(String docId) {
        Optional<Document> optional = documentRepo.findByDocId(docId); //fixme opt
        if (optional.isEmpty()) {
            return false;
        }
        User user = new User();//fixme validation exception - not all field filled
        user.setUsername("USER_DELETED");
        Document document = optional.get();
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

