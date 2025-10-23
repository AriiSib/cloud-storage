package com.khokhlov.cloudstorage.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DirectoryRequest(
        @NotBlank
        @Size(min = 1, max = 300)
        @Pattern(
                regexp = """
                         (?x)
                          ^                                   # start
                          (?!/)                               # not starting with '/'
                          (?!.*//)                            # no double slashes
                          (?!.*(?:^|/)\\.{2}(?:/|$))          # no '..' as a segment
                          (?:[\\p{L}\\p{N}._\\- ]+/)*         # 0+ directory segments
                          [\\p{L}\\p{N}._\\- ]+/              # last segment MUST end with '/'
                          $                                   # end
                        """,
                flags = Pattern.Flag.COMMENTS,
                message = "Invalid path"
        )
        String path
) {
}
