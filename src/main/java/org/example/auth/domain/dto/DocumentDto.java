package org.example.auth.domain.dto;

import com.sun.istack.Nullable;
import lombok.Data;
import lombok.ToString;
import org.example.auth.domain.Tag;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class DocumentDto {
    private String docName;
    private String docId;
    private String filename;
    private String description;
    private Set<Tag> tags;
    private String uploader;
    private LocalDateTime uploadDateTime;
    private LocalDateTime lastEditDateTime;
    private String mediaType;
    private long size;
    private boolean publicDocument;
    @Nullable
    @ToString.Exclude
    private byte[] rawFile;
}
