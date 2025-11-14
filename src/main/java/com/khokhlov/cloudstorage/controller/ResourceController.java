package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.config.security.CustomUserDetails;
import com.khokhlov.cloudstorage.docs.resource.*;
import com.khokhlov.cloudstorage.model.dto.request.*;
import com.khokhlov.cloudstorage.model.dto.response.DownloadResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import com.khokhlov.cloudstorage.service.resource.ResourceCommandService;
import com.khokhlov.cloudstorage.service.resource.ResourceDownloadService;
import com.khokhlov.cloudstorage.service.resource.ResourceQueryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Tag(name = "3. Resources", description = "User files and directories")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResourceController {
    private final ResourceQueryService queryService;
    private final ResourceCommandService commandService;
    private final ResourceDownloadService downloadService;

    @GetResourceDocs
    @GetMapping(value = "/resource", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> getResourceInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path to resource") @Valid @ModelAttribute ResourceRequest request) {

        log.info("GetResourceInfo: user={} path={}", user.getId(), request.path());
        ResourceResponse response = queryService.getResourceInfo(user.getId(), request.path());
        return ResponseEntity.ok(response);
    }

    @GetListDirectoryDocs
    @GetMapping(value = "/directory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceResponse>> listDirectory(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path to directory") @Valid @ModelAttribute RootOrResourceRequest request) {

        log.info("ListDirectory: user={} path={}", user.getId(), request.path());
        List<ResourceResponse> response = queryService.listDirectory(user.getId(), request.path());
        return ResponseEntity.ok().body(response);
    }

    @CreateDirectoryDocs
    @PostMapping(value = "/directory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> createDirectory(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path to directory") @Valid @ModelAttribute RootOrResourceRequest request) {

        log.info("CreateDirectory: user={} path={}", user.getId(), request.path());
        ResourceResponse response = commandService.createDirectory(user.getId(), request.path());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SearchResourceDocs
    @GetMapping(value = "/resource/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ResourceResponse>> searchResource(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Name of resource") @Valid @ModelAttribute(name = "query") ResourceRequest query) {

        log.info("Search: user={} query={}", user.getId(), query.path());
        List<ResourceResponse> response = queryService.searchResource(user.getId(), query.path());
        return ResponseEntity.ok(response);
    }

    @MoveResourceDocs
    @GetMapping(value = "/resource/move", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceResponse> moveResource(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path 'from' and path 'to'") @Valid @ModelAttribute RenameOrMoveRequest request) {

        log.info("Move: user={} from={} to={}", user.getId(), request.from(), request.to());
        ResourceResponse response = commandService.moveResource(user.getId(), request.from(), request.to());
        return ResponseEntity.ok(response);
    }

    @UploadResourceDocs
    @PostMapping(
            value = "/resource",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<ResourceResponse>> uploadResource(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path where to upload resource") @Valid @ModelAttribute RootOrResourceRequest request,
            @Parameter(description = "Files for upload") @RequestParam(name = "object") List<MultipartFile> files) {

        log.info("Upload: user={} path={} files={}", user.getId(), request.path(), files == null ? null : files.size());
        if (files == null || files.isEmpty() || files.stream().anyMatch(file ->
                file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()))
            throw new MultipartException("The file was not transferred to the server");

        List<ResourceResponse> response = commandService.uploadResource(user.getId(), request.path(), files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DownloadResourceDocs
    @GetMapping(value = "/resource/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> downloadResource(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path to the downloaded resource") @Valid @ModelAttribute ResourceRequest request) {

        log.info("Download: user={} path={}", user.getId(), request.path());
        DownloadResponse response = downloadService.downloadResource(user.getId(), request.path());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, response.contentDisposition())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.body());
    }

    @DeleteResourceDocs
    @DeleteMapping(value = "/resource")
    public ResponseEntity<Void> deleteResource(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "Path to the resource to be deleted") @Valid @ModelAttribute ResourceRequest request) {
        log.info("Delete: user={} path={}", user.getId(), request.path());
        commandService.deleteResource(user.getId(), request.path());
        return ResponseEntity.noContent().build();
    }
}
