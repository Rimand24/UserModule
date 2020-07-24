package org.example.auth.service.util;

import org.example.auth.domain.Document;
import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.service.document.DocumentDto;
import org.example.auth.domain.UserDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        dto.setRoles((Set<Role>) user.getAuthorities());//fixme
        dto.setActive(user.isEnabled());
        dto.setBlocked(!user.isAccountNonLocked());
        dto.setRegistrationDate(user.getRegistrationDate());
        dto.setBlockReason(user.getAccountBlockReason());

        List<DocumentDto> docs = new ArrayList<>();

        for (Document d : user.getCreatedDocuments()) {
            docs.add(new DocumentDto() {{
                         setName(d.getName());
                         setDocId(d.getDocId());
                         setSize(d.getSize());
                         setMediaType(d.getMediaType());
                         setCreatedAt(d.getCreatedAt());
                         setFilename(d.getFilename());
                         setCreatedBy(new UserDto() {{
                             setUsername(user.getUsername());
                         }});
                     }}
            );
        }
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

    //fixme change method to modelmapper or make generic transformer
    public DocumentDto mapDocument(Document doc) {
        DocumentDto dto = new DocumentDto();
        dto.setName(doc.getName());
        dto.setDocId(doc.getDocId());
        dto.setFilename(doc.getFilename());
        dto.setMediaType(doc.getMediaType());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setSize(doc.getSize());
        UserDto user = new UserDto() {{
            setUsername(doc.getCreatedBy().getUsername());
            setEmail(doc.getCreatedBy().getEmail());
        }};
        dto.setCreatedBy(user);
        return dto;
    }
}
