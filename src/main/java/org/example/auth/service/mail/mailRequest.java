package org.example.auth.service.mail;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class mailRequest {
    private String subject;
    private String message;
    private String addressee;
    private List<File> files; //fixme file to something more useful
}
