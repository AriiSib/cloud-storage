package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.model.dto.*;
import com.khokhlov.cloudstorage.service.FileService;
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
public class FileController {

    private final FileService fileService;

    @GetMapping(value = "/resource")
    public ResponseEntity<?> check(@Valid @ModelAttribute ResourceRequest request) {
        ResourceResponse response = fileService.checkResource(request.path());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/resource/search")
    public ResponseEntity<?> search(@Valid @ModelAttribute(name = "query") ResourceRequest query) {
        List<ResourceResponse> response = fileService.searchResource(query.path());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/resource/move")
    public ResponseEntity<?> renameOrMove(@Valid @ModelAttribute RenameOrMoveRequest request) {
        ResourceResponse response = fileService.renameOrMove(request.from(), request.to());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/resource", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@Valid @ModelAttribute UploadRequest request,
                                    @RequestPart(name = "file") List<MultipartFile> file) {
        for (MultipartFile fileItem : file) {
            if (fileItem.getOriginalFilename() == null || fileItem.getOriginalFilename().isEmpty())
                throw new MultipartException("The file was not transferred to the server");
        }
        List<ResourceResponse> response = fileService.upload(request.path(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/resource/download")
    public ResponseEntity<StreamingResponseBody> download(@Valid @ModelAttribute ResourceRequest request) {
        DownloadResponse response = fileService.download(request.path());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, response.contentDisposition().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(response.body());
    }

    @DeleteMapping(value = "/resource")
    public ResponseEntity<?> delete(@Valid @ModelAttribute ResourceRequest request) {
        fileService.delete(request.path());
        return ResponseEntity.noContent().build();
    }
}
