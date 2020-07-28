package org.example.auth.controller.file.dto;

import lombok.Data;
import org.example.auth.domain.Tag;

import java.util.HashSet;
import java.util.Set;

@Data
public class DocumentEditForm {
    private String docName;
    private Set<Tag> tagList=new HashSet<>();
    private boolean publicDocument;
}
