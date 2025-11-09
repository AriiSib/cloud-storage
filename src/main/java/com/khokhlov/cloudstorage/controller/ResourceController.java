package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.config.security.CustomUserDetails;
import com.khokhlov.cloudstorage.docs.resource.GetResourceDocs;
import com.khokhlov.cloudstorage.model.dto.request.*;
import com.khokhlov.cloudstorage.model.dto.response.DownloadResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import com.khokhlov.cloudstorage.service.resource.ResourceCommandService;
import com.khokhlov.cloudstorage.service.resource.ResourceDownloadService;
import com.khokhlov.cloudstorage.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "Resources", description = "User files and directories")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResourceController {
    private final ResourceQueryService queryService;
    private final ResourceCommandService commandService;
    private final ResourceDownloadService downloadService;

    @GetResourceDocs
    @GetMapping(value = "/resource", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> getResourceInfo(@AuthenticationPrincipal CustomUserDetails user,
                                                            @Valid @ModelAttribute ResourceRequest request) {
        ResourceResponse response = queryService.getResourceInfo(user.getId(), request.path());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/directory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceResponse>> listDirectory(@AuthenticationPrincipal CustomUserDetails user,
                                                                @Valid @ModelAttribute RootOrResourceRequest request) {
        List<ResourceResponse> response = queryService.listDirectory(user.getId(), request.path());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/directory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> createDirectory(@AuthenticationPrincipal CustomUserDetails user,
                                                            @Valid @ModelAttribute RootOrResourceRequest request) {
        ResourceResponse response = commandService.createDirectory(user.getId(), request.path());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/resource/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceResponse>> searchResource(@AuthenticationPrincipal CustomUserDetails user,
                                                                 @Valid @ModelAttribute(name = "query") ResourceRequest query) {
        List<ResourceResponse> response = queryService.searchResource(user.getId(), query.path());
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/resource/move", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> moveResource(@AuthenticationPrincipal CustomUserDetails user,
                                                         @Valid @ModelAttribute RenameOrMoveRequest request) {
        ResourceResponse response = commandService.moveResource(user.getId(), request.from(), request.to());
        return ResponseEntity.ok(response);
    }

    @PostMapping(
            value = "/resource",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadResource(@AuthenticationPrincipal CustomUserDetails user,
                                            @Valid @ModelAttribute RootOrResourceRequest request,
                                            @RequestParam() List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.stream().anyMatch(
                file -> file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()))
            throw new MultipartException("The file was not transferred to the server");

        List<ResourceResponse> response = commandService.uploadResource(user.getId(), request.path(), files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/resource/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadResource(@AuthenticationPrincipal CustomUserDetails user,
                                                                  @Valid @ModelAttribute ResourceRequest request) {
        DownloadResponse response = downloadService.downloadResource(user.getId(), request.path());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, response.contentDisposition())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.body());
    }

    @DeleteMapping(value = "/resource")
    public ResponseEntity<Void> deleteResource(@AuthenticationPrincipal CustomUserDetails user,
                                               @Valid @ModelAttribute ResourceRequest request) {
        commandService.deleteResource(user.getId(), request.path());
        return ResponseEntity.noContent().build();
    }
}
