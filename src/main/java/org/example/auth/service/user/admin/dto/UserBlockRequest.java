package org.example.auth.service.user.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserBlockRequest implements Serializable {
    private static final long serialVersionUID = 8231778969676638078L;
    @NotNull
    private String username;
    @NotNull
    private String blocker;
    @NotNull
    private String reason;
}
