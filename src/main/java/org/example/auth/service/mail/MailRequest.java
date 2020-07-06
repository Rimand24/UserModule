package org.example.auth.service.mail;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class MailRequest {
    private String subject;
    private String message;
    private String to;
    private List<File> files; //fixme file to something more useful
}
