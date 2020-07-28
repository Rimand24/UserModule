package org.example.auth.controller.file;

import lombok.Data;
import org.example.auth.domain.Tag;

import java.util.Set;

@Data
public class DocumentEditForm {
    private String docName;
    private Set<Tag> tagList;
    private boolean publicDocument;
}
