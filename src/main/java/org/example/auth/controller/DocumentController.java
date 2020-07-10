package org.example.auth.controller;

import org.example.auth.domain.Role;
import org.example.auth.domain.User;
import org.example.auth.service.document.DocumentDto;
import org.example.auth.service.document.DocumentCreationRequestDto;
import org.example.auth.service.document.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        DocumentDto document = documentService.getDocumentFileById(id);
        model.addAttribute("document", document);
  //      ByteArrayResource resource = new ByteArrayResource(document.getRawFile());
//        model.addAttribute("file", resource);


        return "document";
    }

    @GetMapping("/doc/download/{id}")
    public ResponseEntity<Resource> getDocumentFileById(@PathVariable String id) throws IOException {
        DocumentDto documentDto = documentService.getDocumentFileById(id);

        ByteArrayResource resource = new ByteArrayResource(documentDto.getRawFile());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + documentDto.getName())
                      .contentType(MediaType.parseMediaType(documentDto.getMediaType()))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    //fixme search not used yet
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
            DocumentCreationRequestDto request = new DocumentCreationRequestDto();
            request.setCreatedBy(user);
            request.setDocumentFile(file);
            DocumentDto document = documentService.addDocument(request);

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
