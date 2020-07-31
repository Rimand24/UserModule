package org.rimand.doc.controller.file;

import org.rimand.doc.controller.file.dto.DocumentEditForm;
import org.rimand.doc.controller.file.dto.DocumentSearchForm;
import org.rimand.doc.controller.file.dto.DocumentUploadForm;
import org.rimand.doc.domain.User;
import org.rimand.doc.domain.dto.DocumentDto;
import org.rimand.doc.service.document.DocumentService;
import org.rimand.doc.service.document.dto.DocumentCreationRequest;
import org.rimand.doc.service.document.dto.DocumentEditRequest;
import org.rimand.doc.service.document.dto.DocumentSearchRequest;
import org.rimand.doc.service.util.MapperUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
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
    private static final String DOC_EDIT_FORM = "document/editDocumentForm";
    private static final String DOC_INFO = "document/docInfo";
    private static final String DOC_NOT_FOUND = "document/docNotFound";
    private static final String DOC_LIST = "document/docList";
    private static final String REDIRECT_DOCS_ALL = "redirect:/docs/all";
    private static final String REDIRECT_DOCS_ID = "redirect:/docs/{id}";
    //models
    private static final String MODEL_DOC = "doc";
    private static final String MODEL_DOC_LIST = "docList";
    private static final String MODEL_ERROR = "error";
    //message
    private static final String DELETE_ERROR = "delete error";
    private static final String EDIT_ERROR = "edit error";


    public DocumentController(DocumentService documentService, MapperUtils mapper) {
        this.documentService = documentService;
        this.mapper = mapper;
    }

//fixme
//    @PostMapping("/docs/add/fast")
//    public String showAddFast(@AuthenticationPrincipal User user, MultipartFile file) {
//        DocumentCreationRequest request = new DocumentCreationRequest();
//        request.setDocumentFile(file);
//        request.setUploader(user);
//        documentService.create(request);
//        return REDIRECT_DOCS_ALL;
//    }

    //fixme
    @PostMapping("/docs/add")
    public ModelAndView add(@AuthenticationPrincipal User user, @Valid DocumentUploadForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(DOC_LIST, MODEL_ERROR, Arrays.toString(bindingResult.getAllErrors().toArray()));//fixme
        }
        //todo - make handler
        boolean allowed = isAllowedContentType(form.getFile().getContentType());
        if (!allowed) {
            return new ModelAndView(DOC_LIST, MODEL_ERROR, Arrays.toString(bindingResult.getAllErrors().toArray()));//fixme
        }
        System.out.println("controller:"+form);
        DocumentCreationRequest request = mapper.mapDocUploadFormToDocUploadRequest(form, user);
        DocumentDto document = documentService.create(request);
        return new ModelAndView(REDIRECT_DOCS_ALL);
    }

//    @GetMapping("/docs/edit")
//    public ModelAndView editPage() {
//        return new ModelAndView(DOC_EDIT_FORM);
//    }

    @PostMapping("/docs/edit/{docId}")
    public ModelAndView edit(@AuthenticationPrincipal User user, @PathVariable String docId, @Valid DocumentEditForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView(DOC_EDIT_FORM, MODEL_ERROR, Arrays.toString(bindingResult.getAllErrors().toArray()));//fixme
        }
        System.out.println(form);//fixme
        DocumentEditRequest request = mapper.mapDocEditFormToDocEditRequest(form, docId, user);
        Optional<DocumentDto> optional = documentService.edit(request);
        if (optional.isEmpty()) {
            return new ModelAndView(DOC_EDIT_FORM, MODEL_ERROR, EDIT_ERROR);
        }
        return new ModelAndView(DOC_INFO, MODEL_DOC, optional.get());
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
    public ModelAndView findAll() {
        List<DocumentDto> list = documentService.findAll();
        return new ModelAndView(DOC_LIST, MODEL_DOC_LIST, list);
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + document.getDocName())
                .contentType(MediaType.parseMediaType(document.getMediaType()))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/docs/delete/{docId}")
    public ModelAndView delete(@PathVariable String docId, @AuthenticationPrincipal User user) {
        boolean deleted = documentService.delete(docId, user);
        if (!deleted) {
            return new ModelAndView(DOC_LIST, MODEL_ERROR, DELETE_ERROR);
        }
        return new ModelAndView(REDIRECT_DOCS_ALL);
    }

    @PostMapping("/docs/search")
    public ModelAndView search(DocumentSearchForm form) {
      if (!StringUtils.hasText(form.getUsername()) && !StringUtils.hasText(form.getDocName())){
            return new ModelAndView(REDIRECT_DOCS_ALL);
        }

        DocumentSearchRequest request = new DocumentSearchRequest();
        request.setUsername(form.getUsername());
        request.setDocName(form.getDocName());
        List<DocumentDto> list = documentService.searchDocumentsByName(request);
        return new ModelAndView(DOC_LIST, MODEL_DOC_LIST, list);
    }

    @GetMapping("/docs/search}")
    public ModelAndView search() {
            return new ModelAndView(REDIRECT_DOCS_ALL);
    }

//    @PostMapping("/docs/search}")
//    public ModelAndView search(String docName) {
//        if (!StringUtils.hasText(docName)){
//            return new ModelAndView(REDIRECT_DOCS_ALL);
//        }
//        DocumentSearchRequest request = new DocumentSearchRequest();
//        request.setDocName(docName);
//        List<DocumentDto> list = documentService.searchDocumentsByName(request);
//        return new ModelAndView(DOC_LIST, MODEL_DOC_LIST, list);
//    }

    //todo
    private boolean isAllowedContentType(String contentType) {
        return true;
    }

}
