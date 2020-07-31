package org.rimand.doc.controller.file.dto;

import lombok.Data;
import org.rimand.doc.domain.Tag;

import java.util.HashSet;
import java.util.Set;

@Data
public class DocumentEditForm {
    private String docName;
    private Set<Tag> tagList=new HashSet<>();
    private String description;
    private boolean publicDocument;
}
