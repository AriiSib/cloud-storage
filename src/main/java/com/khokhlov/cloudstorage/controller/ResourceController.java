package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.model.dto.request.*;
import com.khokhlov.cloudstorage.model.dto.response.DownloadResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import com.khokhlov.cloudstorage.service.resource.ResourceCommandService;
import com.khokhlov.cloudstorage.service.resource.ResourceDownloadService;
import com.khokhlov.cloudstorage.service.resource.ResourceQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResourceController {
    private final ResourceQueryService queryService;
    private final ResourceCommandService commandService;
    private final ResourceDownloadService downloadService;

    @GetMapping(value = "/resource")
    public ResponseEntity<?> checkResource(@Valid @ModelAttribute ResourceRequest request) {
        ResourceResponse response = queryService.checkResource(request.path());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/directory")
    public ResponseEntity<?> checkDirectory(@Valid @ModelAttribute RootOrResourceRequest request) {
        List<ResourceResponse> response = queryService.checkDirectory(request.path());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/directory")
    public ResponseEntity<?> createDirectory(@Valid @ModelAttribute RootOrResourceRequest request) {
        ResourceResponse response = commandService.createDirectory(request.path());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/resource/search")
    public ResponseEntity<?> search(@Valid @ModelAttribute(name = "query") ResourceRequest query) {
        List<ResourceResponse> response = queryService.searchResource(query.path());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/resource/move")
    public ResponseEntity<?> renameOrMove(@Valid @ModelAttribute RenameOrMoveRequest request) {
        ResourceResponse response = commandService.renameOrMove(request.from(), request.to());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/resource", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@Valid @ModelAttribute RootOrResourceRequest request,
                                    @RequestParam() List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.stream().anyMatch(
                file -> file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()))
            throw new MultipartException("The file was not transferred to the server");

        List<ResourceResponse> response = commandService.upload(request.path(), files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/resource/download")
    public ResponseEntity<StreamingResponseBody> download(@Valid @ModelAttribute ResourceRequest request) {
        DownloadResponse response = downloadService.download(request.path());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, response.contentDisposition())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.body());
    }

    @DeleteMapping(value = "/resource")
    public ResponseEntity<?> delete(@Valid @ModelAttribute ResourceRequest request) {
        commandService.delete(request.path());
        return ResponseEntity.noContent().build();
    }
}
