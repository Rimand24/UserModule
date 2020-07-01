package org.example.auth.service.document;

import lombok.Data;
import org.example.auth.service.user.UserDto;

@Data
public class DocumentDto {

    private String name;

    private String hashedName;

    private String path;

    private UserDto createdBy;
}
