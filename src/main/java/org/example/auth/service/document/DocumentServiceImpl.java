package org.example.auth.service.document;

import lombok.SneakyThrows;
import org.example.auth.domain.Document;
import org.example.auth.repo.DocumentRepo;
import org.example.auth.service.user.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    DocumentRepo documentRepo;

    @Autowired
    ModelMapper mapper;

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
        document.setCreatedBy(request.getCreatedBy());

        Document saved = documentRepo.save(document);

        //fixme: crutch - model mapper exception (failed to convert org.hibernate.collection.internal.PersistentSet to java.util.Set.)
        //return mapper.map(saved, DocumentDto.class);
        DocumentDto documentDto = new DocumentDto();
        documentDto.setName(saved.getName());
        documentDto.setDocId(saved.getDocId());
        documentDto.setFilename(saved.getFilename());
        //maper
        //documentDto.setCreatedBy(mapper.map(saved, UserDto.class));
        UserDto userDto = new UserDto() {{
            setUsername(saved.getCreatedBy().getUsername());
            setEmail(saved.getCreatedBy().getEmail());
        }};
        documentDto.setCreatedBy(userDto);
        return documentDto;
        //end crutch
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
        return mapper.map(document, DocumentDto.class);
    }


    @Override
    public List<DocumentDto> findDocumentsByName(String name) {
        List<Document> documents = documentRepo.findByNameContains(name);
        return getDocumentDtos(documents);
    }

    private List<DocumentDto> getDocumentDtos(List<Document> documents) {
        List<DocumentDto> result = new ArrayList<>();
        if (!documents.isEmpty()) {
            Type listType = new TypeToken<List<DocumentDto>>() {}.getType();
            result = mapper.map(documents, listType);
        }
        return result;
    }

}

