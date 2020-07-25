package org.example.auth.domain;

import com.sun.istack.Nullable;
import lombok.Data;
import lombok.ToString;
import org.example.auth.domain.UserDto;

import java.time.LocalDateTime;

@Data
public class DocumentDto {

    private String name;
    private String docId;
    private String filename;
    private String author;
    private LocalDateTime creationDateTime;
    private String mediaType;
    private long size;
    @Nullable
    @ToString.Exclude
    private byte[] rawFile;
}
