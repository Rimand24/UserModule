package org.example.auth.service.document;

import org.example.auth.domain.Document;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.service.ResourceService;
import org.example.auth.service.user.UserDto;
import org.example.auth.service.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepo documentRepo;

//    @Autowired
//    ModelMapper mapper = new ModelMapper();

    @Autowired
    ResourceService resourceService;

    @Autowired
    private UUIDGenerator generator;

    @Override
    public DocumentDto addDocument(DocumentRequest request) {

        if (request == null || request.getCreatedBy() == null || request.getDocumentFile() == null){
         throw new DocumentServiceException("document addition request incorrect");
        }

        MultipartFile file = request.getDocumentFile();
        String filename = file.getOriginalFilename();
        String docId = generator.generateUUID();
        String resultFilename = docId + "." + filename;
        resourceService.saveFile(file, resultFilename);

        Document document = new Document();
        document.setName(filename);
        document.setDocId(docId);
        document.setFilename(resultFilename);
        //todo add ContentType() to Document entity
        //document.setContentType(file.getContentType());
        document.setCreatedBy(request.getCreatedBy());
        Document saved = documentRepo.save(document);

        //fixme: model mapper exception (failed to convert org.hibernate.collection.internal.PersistentSet to java.util.Set.)
        //return mapper.map(saved, DocumentDto.class);
        return docToDtoMapping(saved);
    }

    public List<DocumentDto> getAllDocuments() {
        List<Document> documents = documentRepo.findAll();
        return getDocumentDtos(documents);
    }

    @Override
    public DocumentDto getDocumentById(String docId) {
        Document document = documentRepo.findByDocId(docId);
        if (StringUtils.isEmpty(document)) {
            throw new DocumentServiceException("document not found");  //fixme make custom exception
        }
        return docToDtoMapping(document);
    }

    @Override
    public List<DocumentDto> findDocumentsByName(String name) {
        List<Document> documents = documentRepo.findByNameContains(name);
        return getDocumentDtos(documents);
    }

    @Override
    public Path getDocumentFileById(String id) {
        Document document = documentRepo.findByDocId(id);
        if (document == null) {
            throw new DocumentServiceException("document not found (id:" + id + ")");
        }
        return resourceService.getFile(document.getFilename());
    }

    @Override
    public boolean deleteDocument(String id) {
        Document document = documentRepo.findByDocId(id);
        documentRepo.delete(document);
        resourceService.deleteFile(document.getFilename());
        return true;
    }

    private List<DocumentDto> getDocumentDtos(List<Document> documents) {
        List<DocumentDto> result = new ArrayList<>();

        for (Document doc : documents) {
            DocumentDto dto = docToDtoMapping(doc);
            result.add(dto);
        }

        return result;
    }

    //fixme change method to modelmapper or make generic transformer
    private DocumentDto docToDtoMapping(Document doc) {
        DocumentDto dto = new DocumentDto();
        dto.setName(doc.getName());
        dto.setDocId(doc.getDocId());
        dto.setFilename(doc.getFilename());
        UserDto user = new UserDto() {{
            setUsername(doc.getCreatedBy().getUsername());
            setEmail(doc.getCreatedBy().getEmail());
        }};
        dto.setCreatedBy(user);
        return dto;
    }
}

