package org.example.auth.service.user;

import lombok.Data;
import org.example.auth.service.document.DocumentDto;

import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private String username;
    private String email;
    Set<DocumentDto> createdDocuments;
}
