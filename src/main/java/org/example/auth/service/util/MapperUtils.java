package org.example.auth.service.util;

import org.example.auth.controller.file.dto.DocumentEditForm;
import org.example.auth.service.document.dto.DocumentEditRequest;
import org.example.auth.controller.file.dto.DocumentUploadForm;
import org.example.auth.domain.Document;
import org.example.auth.domain.User;
import org.example.auth.domain.dto.DocumentDto;
import org.example.auth.domain.dto.UserDto;
import org.example.auth.service.document.dto.DocumentCreationRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapperUtils {

    public List<UserDto> mapUserList(List<User> users) {
        List<UserDto> dtoList = new ArrayList<>();
        for (User u : users) {
            UserDto dto = mapUser(u);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public UserDto mapUser(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getAuthorities());
        dto.setActive(user.isEnabled());
        dto.setBlocked(!user.isAccountNonLocked());
        dto.setRegistrationDate(user.getRegistrationDate());
        dto.setBlockReason(user.getAccountBlockReason());
        dto.setBlockDate(user.getAccountBlockDate());
        dto.setBlockerName(user.getAccountBlockerName());

        List<DocumentDto> docs = user.getCreatedDocuments().stream().map(doc -> new DocumentDto() {
            {
                setDocName(doc.getDocName());
                setDocId(doc.getDocId());
                setSize(doc.getSize());
                setMediaType(doc.getMediaType());
                setUploadDateTime(doc.getUploadDateTime());
                setFilename(doc.getFilename());
                setUploader(user.getUsername());
            }
        }).collect(Collectors.toList());

        dto.setCreatedDocuments(docs);
        return dto;
    }

    public List<DocumentDto> mapDocumentListToDtoList(List<Document> documents) {
        List<DocumentDto> result = new ArrayList<>();
        for (Document doc : documents) {
            DocumentDto dto = mapDocumentToDto(doc);
            result.add(dto);
        }
        return result;
    }

    public DocumentDto mapDocumentToDto(Document doc) {
        System.out.println("mapper sout debug"); //fixme
        System.out.println("name:"+doc.getDocName()+"\nupload date:"+doc.getUploadDateTime()); //fixme
        DocumentDto dto = new DocumentDto();
        dto.setDocName(doc.getDocName());
        dto.setDocId(doc.getDocId());
        dto.setFilename(doc.getFilename());
        dto.setMediaType(doc.getMediaType());
        dto.setUploadDateTime(doc.getUploadDateTime());
        dto.setSize(doc.getSize());
        dto.setUploader(doc.getUploader().getUsername());
        dto.setLastEditDateTime(doc.getLastEditDateTime());
        dto.setTags(doc.getTags());
        dto.setDescription(doc.getDescription());
        System.out.println(dto.toString()); //fixme
        return dto;
    }

    public DocumentCreationRequest mapDocUploadFormToDocUploadRequest(DocumentUploadForm form, User user) {
        DocumentCreationRequest request = new DocumentCreationRequest();
        request.setDocName(form.getDocName());
        request.setUploader(user);
        request.setDescription(form.getDescription());
        request.setPublicDocument(form.isPublicDocument());
        request.setTags(form.getTags());
        request.setDocumentFile(form.getFile());
        return request;
    }

    public DocumentEditRequest mapDocEditFormToDocEditRequest(DocumentEditForm form, String docId, User user) {
        DocumentEditRequest request = new DocumentEditRequest();
        request.setDocId(docId);
        request.setDocName(form.getDocName());
        request.setPublicDocument(form.isPublicDocument());
        request.setTags(form.getTagList());
        request.setEditor(user);
        return request;
    }
}
