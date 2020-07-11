package org.example.auth.service.document;

import lombok.SneakyThrows;
import org.example.auth.domain.Document;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.service.storage.StorageService;
import org.example.auth.service.util.MapperUtils;
import org.example.auth.service.util.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private StorageService storageService;

    @Autowired
    private RandomStringGenerator generator;

    @Autowired
    private MapperUtils mapper;

    @SneakyThrows
    @Override
    public DocumentDto addDocument(DocumentCreationRequestDto request) {

        if (request == null || request.getCreatedBy() == null || request.getDocumentFile() == null) {
            throw new DocumentServiceException("document addition request incorrect");
        }

        MultipartFile multipartFile = request.getDocumentFile();
        String name = multipartFile.getOriginalFilename();
        String docId = generator.generateUUID();
        String filename = storageService.save(multipartFile.getBytes(), name, docId);

        Document document = new Document();
        document.setName(name);
        document.setDocId(docId);
        document.setFilename(filename);
        document.setMediaType(multipartFile.getContentType());
        document.setSize(multipartFile.getSize());
        document.setCreatedBy(request.getCreatedBy());
        document.setCreatedAt(LocalDateTime.now());
        Document saved = documentRepo.save(document);

        return mapper.docToDtoMapping(saved);
    }

    public List<DocumentDto> getAllDocuments() {
        List<Document> documents = documentRepo.findAll();
        return mapper.getDocumentDtos(documents);
    }

    @Override
    public DocumentDto getDocumentById(String docId) {
        Document document = documentRepo.findByDocId(docId);
        if (StringUtils.isEmpty(document)) {
            throw new DocumentServiceException("document not found (id:" + docId + ")");
        }
        return mapper.docToDtoMapping(document);
    }

    @Override
    public List<DocumentDto> findDocumentsByName(String name) {
        List<Document> documents = documentRepo.findByNameContains(name);
        return mapper.getDocumentDtos(documents);
    }

    @SneakyThrows
    @Override
    public DocumentDto getDocumentFileById(String docId) {
        DocumentDto document = getDocumentById(docId);
        byte[] load = storageService.load(document.getFilename());
        document.setRawFile(load);
        return document;
    }

    @Override
    public boolean deleteDocument(String docId) {
        Document document = documentRepo.findByDocId(docId);
        documentRepo.delete(document);
        storageService.delete(document.getFilename());
        return true;
    }

}

