package org.example.auth.controller.file;

import org.example.auth.controller.file.dto.DocumentEditForm;
import org.example.auth.controller.file.dto.DocumentSearchForm;
import org.example.auth.controller.file.dto.DocumentUploadForm;
import org.example.auth.domain.User;
import org.example.auth.domain.dto.DocumentDto;
import org.example.auth.service.document.dto.DocumentCreationRequest;
import org.example.auth.service.document.dto.DocumentEditRequest;
import org.example.auth.service.document.dto.DocumentSearchRequest;
import org.example.auth.service.document.DocumentService;
import org.example.auth.service.util.MapperUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class DocumentController {

    private final DocumentService documentService;
    private final MapperUtils mapper;

    //view names
    private static final String DOC_ADD_FORM = "document/addDocumentForm";
    private static final String DOC_EDIT_FORM = "document/editDocumentForm";
    private static final String SEARCH_FORM = "document/searchDocumentForm";
    private static final String DOC_INFO = "document/docInfo";
    private static final String DOC_NOT_FOUND = "document/docNotFound";
    private static final String DOC_LIST = "document/docList";
    private static final String REDIRECT_DOCS_ALL = "redirect:/docs/all";
    //models
    private static final String MODEL_DOC = "doc";
    private static final String MODEL_DOC_LIST = "docList";
    private static final String MODEL_ERROR = "error";
    //message
    private static final String DELETE_FAILED = "delete failed";
    private static final String EDIT_ERROR = "edit error";


    public DocumentController(DocumentService documentService, MapperUtils mapper) {
        this.documentService = documentService;
        this.mapper = mapper;
    }


    @GetMapping("/docs/add")
    public String showAddPage() {
        return DOC_ADD_FORM;
    }

    @PostMapping("/docs/add")
    public ModelAndView add(@AuthenticationPrincipal User user, @Valid DocumentUploadForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(DOC_ADD_FORM, MODEL_ERROR, Arrays.toString(bindingResult.getAllErrors().toArray()));//fixme
        }
        DocumentCreationRequest request = mapper.mapDocUploadFormToDocUploadRequest(form, user);
        DocumentDto document = documentService.create(request);
        return new ModelAndView(DOC_INFO, MODEL_DOC, document);
    }

    @GetMapping("/docs/edit")
    public ModelAndView editPage() {
        return new ModelAndView(DOC_EDIT_FORM);
    }

    @PostMapping("/docs/edit/{docId}")
    public ModelAndView edit(@AuthenticationPrincipal User user, @PathVariable String docId, @Valid DocumentEditForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(DOC_EDIT_FORM, MODEL_ERROR, Arrays.toString(bindingResult.getAllErrors().toArray()));//fixme
        }
        DocumentEditRequest request = mapper.mapDocEditFormToDocEditRequest(form, docId, user);
        Optional<DocumentDto> optional = documentService.edit(request);
        if (optional.isEmpty()) {
            return new ModelAndView(DOC_EDIT_FORM, MODEL_ERROR, EDIT_ERROR);
        }
        return new ModelAndView(DOC_INFO, MODEL_DOC, optional.get());
    }

    @DeleteMapping("/docs/{docId}")
    public ModelAndView delete(@PathVariable String docId, @AuthenticationPrincipal User user) {
        boolean deleted = documentService.delete(docId, user);
        if (!deleted) {
            return new ModelAndView(DOC_LIST, MODEL_ERROR, DELETE_FAILED);
        }
        return new ModelAndView(REDIRECT_DOCS_ALL);
    }

    @GetMapping("/docs/{docId}")
    public ModelAndView findById(@PathVariable String docId) {
        Optional<DocumentDto> optional = documentService.findById(docId);
        if (optional.isEmpty()) {
            return new ModelAndView(DOC_NOT_FOUND, MODEL_ERROR, docId);
        }
        return new ModelAndView(DOC_INFO, MODEL_DOC, optional.get());
    }

    @GetMapping("/docs/all")
    public ModelAndView findAll(Model model) {
        List<DocumentDto> list = documentService.findAll();
        model.addAttribute(DOC_LIST, list);
        return new ModelAndView(DOC_INFO, MODEL_DOC_LIST, list);
    }

    @GetMapping("/docs/download/{docId}")
    public ResponseEntity<Resource> downloadFileById(@PathVariable String docId) throws IOException {
        Optional<DocumentDto> optional = documentService.downloadDocById(docId);
        if (optional.isEmpty()) {
//            return new ModelAndView(DOC_NOT_FOUND, MODEL_ERROR, docId); //fixme ???
        }
        DocumentDto document = optional.get();
        ByteArrayResource resource = new ByteArrayResource(document.getRawFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + document.getName())
                .contentType(MediaType.parseMediaType(document.getMediaType()))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/docs/search")
    public ModelAndView search() {
        return new ModelAndView(SEARCH_FORM);
    }

    @PostMapping("/docs/search")
    public ModelAndView search(DocumentSearchForm form) {
        DocumentSearchRequest request = new DocumentSearchRequest();
        request.setUsername(form.getUsername());
        request.setDocName(form.getDocName());
        List<DocumentDto> list = documentService.searchDocumentsByName(request);
        return new ModelAndView(DOC_INFO, MODEL_DOC_LIST, list);
    }

}
