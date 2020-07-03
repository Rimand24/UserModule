package org.example.auth.service.document;

import lombok.SneakyThrows;
import org.example.auth.domain.Document;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.service.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    DocumentRepo documentRepo;

//    @Autowired
//    ModelMapper mapper = new ModelMapper();

    @Value("${upload.path}")
    private String uploadPath;

    @SneakyThrows
    @Override
    public DocumentDto addDocument(DocumentRequest request) {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        MultipartFile file = request.getDocumentFile();
        String filename = file.getOriginalFilename();
        String docId = UUID.randomUUID().toString();
        String resultFilename = docId + "." + filename;
        file.transferTo(new File(uploadPath + "/" + resultFilename));

        Document document = new Document();
        document.setName(filename);
        document.setDocId(docId);
        document.setFilename(resultFilename);
        //todo add ContentType() to Document entity
        //document.setContentType(file.getContentType());
        document.setCreatedBy(request.getCreatedBy());

        Document saved = documentRepo.save(document);

        //fixme: crutch - model mapper exception (failed to convert org.hibernate.collection.internal.PersistentSet to java.util.Set.)
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
            throw new RuntimeException("document not found");  //fixme make custom exception
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
        File file = new File(uploadPath + "/" + document.getFilename());
        return file.toPath();
    }

    @Override
    public boolean deleteDocument(String id) {
        Document document = documentRepo.findByDocId(id);
        documentRepo.delete(document);

        File file = new File(uploadPath + "/" + document.getFilename());
         if( file.exists()){
             file.delete();
         }
        return true;
    }



    private List<DocumentDto> getDocumentDtos(List<Document> documents) {
        List<DocumentDto> result = new ArrayList<>();

        //fixme maping dont work
//        if (!documents.isEmpty()) {
//            Type listType = new TypeToken<List<DocumentDto>>() {}.getType();
//            result = mapper.map(documents, listType);
//        }
        for (Document doc : documents){
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
        UserDto user = new UserDto(){{setUsername(doc.getCreatedBy().getUsername());setEmail(doc.getCreatedBy().getEmail());}};
        dto.setCreatedBy(user);
        return dto;
    }



}

