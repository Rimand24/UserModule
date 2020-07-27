package org.example.auth.domain.dto;

import com.sun.istack.Nullable;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
public class DocumentDto {

    private String name;
    private String docId;
    private String filename;
    private String uploader;
    private LocalDateTime uploadDateTime;
    private String mediaType;
    private long size;
    @Nullable
    @ToString.Exclude
    private byte[] rawFile;
}
