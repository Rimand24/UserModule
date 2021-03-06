package org.rimand.doc.domain.dto;

import lombok.Data;
import org.rimand.doc.domain.Role;

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
    private String blockerName;
    private String blockReason;
    private LocalDate blockDate;
}
