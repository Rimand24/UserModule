package org.rimand.doc.controller.user.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserBlockForm {
    @NotNull
    private String username;
    @NotNull
    private String reason;
}
