package org.example.auth.service.user;

import lombok.Data;
import org.example.auth.domain.Role;
import org.example.auth.service.document.DocumentDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class UserDto {
    private String username;
    private String email;
    private LocalDate registrationDate;
    private List<DocumentDto> createdDocuments;
    private Set<Role> roles;
    private boolean active;
    private boolean blocked;
    private String blockReason;
}
