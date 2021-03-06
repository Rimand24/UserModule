package org.rimand.doc.service.user.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserBlockRequest {
    @NotNull
    private String username;
    @NotNull
    private String blocker;
    @NotNull
    private String reason;
}
