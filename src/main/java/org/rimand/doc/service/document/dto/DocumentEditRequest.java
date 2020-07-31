package org.rimand.doc.service.document.dto;

import lombok.Data;
import org.rimand.doc.domain.Tag;
import org.rimand.doc.domain.User;


import javax.validation.constraints.NotNull;
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
