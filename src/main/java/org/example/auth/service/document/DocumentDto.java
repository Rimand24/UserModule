package org.example.auth.service.document;

import com.sun.istack.Nullable;
import lombok.Data;
import lombok.ToString;
import org.example.auth.service.user.UserDto;

import java.time.LocalDateTime;

@Data
public class DocumentDto {

    private String name;
    private String docId;
    private String filename;
    private UserDto createdBy;
    private LocalDateTime createdAt;
    private String mediaType;
    private long size;
    @Nullable
    @ToString.Exclude
    private byte[] rawFile;
}
