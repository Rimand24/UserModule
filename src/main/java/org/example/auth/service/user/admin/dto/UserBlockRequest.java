package org.example.auth.service.user.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserBlockRequest {
    @NotNull
    private String username;
    @NotNull
    private String blocker;
    @NotNull
    private String reason;
}
