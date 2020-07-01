package org.example.auth.service.document;

import org.example.auth.controller.requestDto.DocumentForm;
import org.example.auth.domain.Document;
import org.example.auth.repo.DocumentRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    DocumentRepo documentRepo;

    @Autowired
    ModelMapper mapper;


    public List<DocumentDto> getAllDocuments() {

        List<Document> documents = documentRepo.findAll();
        List<DocumentDto> result = new ArrayList<>();
        if (!documents.isEmpty()) {
            Type listType = new TypeToken<List<DocumentDto>>() {
            }.getType();
            result = mapper.map(documents, listType);
        }
        return result;

    }

    @Override
    public DocumentDto getDocument(String hashedName) {
        Document document = documentRepo.findByHashedName(hashedName);
        if (StringUtils.isEmpty(document)){
            throw new RuntimeException("document not found");  //fixme make custom exception
        }
        DocumentDto result = mapper.map(document, DocumentDto.class);
        return result;
    }

    @Override
    public DocumentDto addDocument(DocumentForm form) {

        Document document = mapper.map(form, Document.class);
        Document save = documentRepo.save(document);

        return mapper.map(save, DocumentDto.class);

    }
}

