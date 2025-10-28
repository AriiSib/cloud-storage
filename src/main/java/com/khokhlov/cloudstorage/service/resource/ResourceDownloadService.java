package com.khokhlov.cloudstorage.service.resource;

import com.khokhlov.cloudstorage.adapter.StoragePort;
import com.khokhlov.cloudstorage.exception.minio.StorageNotFoundException;
import com.khokhlov.cloudstorage.facade.CurrentUser;
import com.khokhlov.cloudstorage.model.dto.response.DownloadResponse;
import com.khokhlov.cloudstorage.model.dto.response.MinioResponse;
import com.khokhlov.cloudstorage.util.PathUtil;
import com.khokhlov.cloudstorage.util.StorageObjectBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ResourceDownloadService {
    private final CurrentUser currentUser;
    private final StoragePort storage;

    public DownloadResponse download(String path) {
        Long userId = currentUser.getCurrentUserId();
        String userRoot = StorageObjectBuilder.getUserRoot(userId);
        String objectName = userRoot + path;
        boolean isDir = path.endsWith("/");
        if (isDir) {
            if (!storage.isResourceExists(objectName))
                throw new StorageNotFoundException("Resource not found");
            else {
                List<String> objects = storage.listObjects(objectName, true);
                String zipName = PathUtil.getDirName(path) + ".zip";

                StreamingResponseBody body = out -> {
                    try (ZipOutputStream zip = new ZipOutputStream(out)) {
                        for (String filePath : objects) {
                            String relPath = filePath.substring(userRoot.length());
                            String entryName = relPath.substring(path.length());
                            ZipEntry entry = new ZipEntry(entryName);
                            zip.putNextEntry(entry);
                            try (InputStream in = storage.download(filePath)) {
                                in.transferTo(zip);
                            }
                            zip.closeEntry();
                        }
                    }
                };

                return new DownloadResponse(body, buildContentDisposition(zipName));
            }
        } else {
            MinioResponse meta = storage.checkObject(objectName);
            if (meta == null)
                throw new StorageNotFoundException("Resource not found");

            StreamingResponseBody body = out -> {
                try (InputStream in = storage.download(objectName)) {
                    in.transferTo(out);
                }
            };

            return new DownloadResponse(body, buildContentDisposition(PathUtil.getFileName(path)));
        }
    }

    private String buildContentDisposition(String resourceName) {
        String encodedFileName = URLEncoder.encode(resourceName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "attachment; filename*=utf-8''" + encodedFileName;
    }

}
