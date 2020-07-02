package org.example.auth.controller;

import org.example.auth.domain.User;
import org.example.auth.service.document.DocumentDto;
import org.example.auth.service.document.DocumentRequest;
import org.example.auth.service.document.DocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    DocumentService documentService;

    @GetMapping
    public String getAllDocuments(Model model) {
        List<DocumentDto> list = documentService.getAllDocuments();
        model.addAllAttributes(list);
        return "documentList";
    }

    @GetMapping("/doc/{id}")
    public String getDocument(@PathVariable String id) {
        documentService.getDocumentById(id);
        return "document";
    }

    @PostMapping("/doc/add")
    public String addDocument(@AuthenticationPrincipal User user, //fixme user????
                              Model model,
                             // BindingResult bindingResult, //fixme BindingResult ????
                              @RequestParam("file") MultipartFile file) {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            DocumentRequest request = new DocumentRequest();
            request.setCreatedBy(user);
            request.setDocumentFile(file);
            DocumentDto document = documentService.addDocument(request);
            model.addAttribute("document", document);
        } else {
            model.addAttribute("error", "filenotfound");//todo exception
        }

        System.out.println("saved");
        return "document";
    }

}
