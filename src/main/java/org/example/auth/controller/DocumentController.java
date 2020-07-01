package org.example.auth.controller;

import org.example.auth.controller.requestDto.DocumentForm;
import org.example.auth.service.document.DocumentDto;
import org.example.auth.service.document.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class DocumentController {

    @Autowired
    DocumentService documentService;

    @GetMapping
    public String getAllDocuments(Model model){
        List<DocumentDto> list = documentService.getAllDocuments();
        model.addAllAttributes(list);
        return "documentList";
    }

    @GetMapping("/doc/{id}")
    public String getDocument(@PathVariable String id){
        documentService.getDocument(id);
        return "document";
    }

    @PostMapping("/doc/add")
    public String addDocument(DocumentForm form){

documentService.addDocument(form)  ;
        return "document";
    }

}
