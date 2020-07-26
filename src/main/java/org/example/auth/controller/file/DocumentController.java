package org.example.auth.controller.file;

import org.example.auth.domain.DocumentDto;
import org.example.auth.domain.User;
import org.example.auth.service.document.DocumentCreationRequestDto;
import org.example.auth.service.document.DocumentService;
import org.example.auth.service.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class DocumentController {



    @Autowired
    private DocumentService documentService;

    @Autowired
    private MapperUtils mapper;

    //view names
    private static final String DOC_LIST = "document/docList";//fixme
    private static final String DOC_ADD_FORM = "document/addDocumentForm";//fixme
    private static final String DOC_INFO = "document/docInfo";//fixme
    private static final String DOC_NOT_FOUND = "document/docNotFound";//fixme
    private static final String REDIRECT = "redirect:/docs/all";
    //models
    private static final String MODEL_DOC = "doc";
    private static final String MODEL_DOC_LIST = "docList";
    private static final String MODEL_ERROR = "error";
    //message
    private static final String DELETE_FAILED = "delete failed";

    @GetMapping
    public String addDocPage() {
        return DOC_ADD_FORM;
    }

    @PostMapping("/docs/add")
    public ModelAndView addDocument(@AuthenticationPrincipal User user, BindingResult bindingResult,
                                    @Valid DocsUploadForm form) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(DOC_ADD_FORM, MODEL_ERROR, Arrays.toString(bindingResult.getAllErrors().toArray()));//fixme
        }

        DocumentCreationRequestDto request = mapper.mapDocFormToDocRequest(form, user);
        DocumentDto document = documentService.addDocument(request);
        return new ModelAndView(DOC_INFO, MODEL_DOC, document);
    }

    @GetMapping("/docs/all")
    public ModelAndView getAllDocuments(Model model) {
        List<DocumentDto> list = documentService.getAllDocuments();
        model.addAttribute("documentList", list);
        return new ModelAndView(DOC_INFO, MODEL_DOC_LIST, list);
    }

    @GetMapping("/docs/{id}")
    public ModelAndView getDocumentByPathVariable(@PathVariable String id) {
        DocumentDto document = documentService.getDocumentFileById(id);
        return new ModelAndView(DOC_INFO, MODEL_DOC, document);
    }

    @GetMapping("/docs/download/{id}")
    public ResponseEntity<Resource> getDocumentFileById(@PathVariable String id) throws IOException {
        DocumentDto documentDto = documentService.getDocumentFileById(id);
        ByteArrayResource resource = new ByteArrayResource(documentDto.getRawFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + documentDto.getName())
                .contentType(MediaType.parseMediaType(documentDto.getMediaType()))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PostMapping("/docs/search")
    public ModelAndView searchDocumentsByName(@RequestParam String name) {
        List<DocumentDto> list = documentService.searchDocumentsByName(name);
        return new ModelAndView(DOC_INFO, MODEL_DOC_LIST, list);
    }

    @DeleteMapping("/docs/{id}")
    public ModelAndView deleteDocument(@PathVariable String id) {
        boolean deleted = documentService.deleteDocument(id);
        if (!deleted) {
            return new ModelAndView(DOC_LIST, MODEL_ERROR, DELETE_FAILED);
        }
        return new ModelAndView(REDIRECT);
    }

}
