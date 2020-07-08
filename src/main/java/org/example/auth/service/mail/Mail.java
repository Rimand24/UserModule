package org.example.auth.service.mail;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
public class Mail {
    private String subject;
    private String content;
    private String to;
    private List<File> files; //fixme file to something more useful

    void addFile(File file) {
        if (files == null) files = new ArrayList<>();
        files.add(file);
    }
}
