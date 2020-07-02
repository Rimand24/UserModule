package org.example.auth.service.document;

import lombok.Data;
import org.example.auth.domain.User;
import org.example.auth.service.user.UserDto;

@Data
public class DocumentDto {

    private String name;

    private String docId;

    private String filename;

    private UserDto createdBy;
}
