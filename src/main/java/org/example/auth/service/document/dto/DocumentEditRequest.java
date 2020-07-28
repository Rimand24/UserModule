package org.example.auth.service.document.dto;

import lombok.Data;
import org.example.auth.domain.Tag;
import org.example.auth.domain.User;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class DocumentEditRequest {
    @NotNull
    private String docId;
    private String docName;
    private Set<Tag> tags;
    private boolean publicDocument;
    @NotNull
    private User editor;
}
