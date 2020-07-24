package org.example.auth.controller.user.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserBlockForm {
    @NotNull
    private String username;
    @NotNull
    private String reason;
}
