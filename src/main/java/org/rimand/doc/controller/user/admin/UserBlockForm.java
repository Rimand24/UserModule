package org.rimand.doc.controller.user.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserBlockForm implements Serializable {
    private static final long serialVersionUID = 7752755584724143768L;
    @NotNull
    private String username;
    @NotNull
    private String reason;
}
