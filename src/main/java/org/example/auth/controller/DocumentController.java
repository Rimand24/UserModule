package org.example.auth.controller;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.impl.FileItemStreamImpl;
import org.example.auth.domain.User;
import org.example.auth.service.document.DocumentDto;
import org.example.auth.service.document.DocumentRequest;
import org.example.auth.service.document.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    DocumentService documentService;

    @GetMapping("/doc/all")
    public String getAllDocuments(Model model) {
        List<DocumentDto> list = documentService.getAllDocuments();
        model.addAttribute("documentList", list);
        return "documentList";
    }

    @GetMapping("/doc/{id}")
    public String getDocumentByPathVariable(@PathVariable String id, Model model) {
        DocumentDto document = documentService.getDocumentById(id);
        model.addAttribute("document", document);
        return "document";
    }

    @GetMapping("/doc/download/{id}")
    public ResponseEntity<InputStreamResource> getDocumentFileById(@PathVariable String id) throws IOException {
        File file = documentService.getDocumentFileById(id);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        String mimeType = Files.probeContentType(file.toPath());
        MediaType mediaType = MediaType.valueOf(mimeType);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(mediaType)
                .contentLength(file.length()) //
                .body(resource);
    }

    //fixme not used yet
    @PostMapping("/doc/find/")
    public String findDocumentsByName(@RequestParam String name, Model model) {
        List<DocumentDto> documents = documentService.findDocumentsByName(name);
        model.addAttribute("documentList", documents);
        return "documentList";
    }

    @PostMapping("/doc/add")
    public String addDocument(@AuthenticationPrincipal User user,
                              Model model,
                              // BindingResult bindingResult, //fixme BindingResult ????
                              @RequestParam("file") MultipartFile file) {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            DocumentRequest request = new DocumentRequest();
            request.setCreatedBy(user);
            request.setDocumentFile(file);
            DocumentDto document = documentService.addDocument(request);
//            model.addAttribute("document", document);
        } else {
            model.addAttribute("error", "filenotfound");//todo exception
        }
        return "redirect:/doc/all";
    }

    @GetMapping("/doc/delete/{id}")
    public String deleteDocument(@PathVariable String id, Model model) {
        boolean deleted = documentService.deleteDocument(id);
        if (!deleted) {
            model.addAttribute("error", "deleteFailed");
        }
        return "redirect:/doc/all";
    }


}
