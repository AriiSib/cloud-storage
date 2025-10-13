package com.khokhlov.cloudstorage.controller;

import com.khokhlov.cloudstorage.model.dto.UploadRequest;
import com.khokhlov.cloudstorage.model.dto.UploadResponse;
import com.khokhlov.cloudstorage.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/resource", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@Valid @ModelAttribute UploadRequest request,
                                    @RequestPart(name = "file") List<MultipartFile> file) {
        for (MultipartFile fileItem : file) {
            if (file.isEmpty() || fileItem.getOriginalFilename() == null || fileItem.getOriginalFilename().isEmpty()) {
                throw new MultipartException("The file was not transferred to the server");
            }
        }
        List<UploadResponse> response = fileService.upload(request.path(), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
