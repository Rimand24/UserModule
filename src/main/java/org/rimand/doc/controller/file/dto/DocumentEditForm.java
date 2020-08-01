package org.rimand.doc.controller.file.dto;

import lombok.Data;
import org.rimand.doc.domain.Tag;

import java.util.HashSet;
import java.util.Set;

@Data
public class DocumentEditForm {
    private String docName;
//    private Set<Tag> tags =new HashSet<>(); //fixme
    private String tags;
    private String description;
    private boolean publicDocument;
}
