package com.khokhlov.cloudstorage.model.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UploadRequest(
        @Size(max = 300)
        @Pattern(
                regexp = """
                        (?x)
                        ^$                                # allow empty path = root
                        |                                 # OR a normal directory path:
                        ^                                  # start
                        (?!/)                              # not starting with '/'
                        (?!.*//)                           # no double slashes
                        (?!.*(?:^|/)\\.{2}(?:/|$))         # forbid '..' as a segment
                        (?:[A-Za-z0-9._-]+/)+              # 1+ segments, MUST end with '/'
                        $                                  # end
                        """,
                flags = jakarta.validation.constraints.Pattern.Flag.COMMENTS,
                message = "Path must be empty (root) or a directory ending with '/', '..' and '//' are not allowed"
        )
        String path
) {
}
