package org.example.auth.controller.file;

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
    private Set<Tag> tags = new HashSet<>();
    private boolean publicDocument;
    private User editor;
}
