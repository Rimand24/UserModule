package org.example.auth.service.util;

import org.example.auth.controller.file.DocsUploadForm;
import org.example.auth.domain.Document;
import org.example.auth.domain.DocumentDto;
import org.example.auth.domain.User;
import org.example.auth.domain.UserDto;
import org.example.auth.service.document.DocumentCreationRequestDto;
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

        List<DocumentDto> docs = user.getCreatedDocuments().stream().map(doc -> new DocumentDto() {
            {
                setName(doc.getName());
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

    public List<DocumentDto> mapDocumentList(List<Document> documents) {
        List<DocumentDto> result = new ArrayList<>();
        for (Document doc : documents) {
            DocumentDto dto = mapDocument(doc);
            result.add(dto);
        }
        return result;
    }

    public DocumentDto mapDocument(Document doc) {
        DocumentDto dto = new DocumentDto();
        dto.setName(doc.getName());
        dto.setDocId(doc.getDocId());
        dto.setFilename(doc.getFilename());
        dto.setMediaType(doc.getMediaType());
        dto.setUploadDateTime(doc.getUploadDateTime());
        dto.setSize(doc.getSize());
        dto.setUploader(doc.getUploader().getUsername());
        return dto;
    }

    public DocumentCreationRequestDto mapDocFormToDocRequest(DocsUploadForm form, User user) {
        DocumentCreationRequestDto requestDto = new DocumentCreationRequestDto();
        requestDto.setDocumentFile(form.getFile());
        requestDto.setCreatedBy(user); //todo User to String?
return requestDto;
    }
}
