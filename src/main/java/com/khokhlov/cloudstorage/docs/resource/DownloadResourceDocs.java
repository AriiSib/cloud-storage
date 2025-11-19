package com.khokhlov.cloudstorage.docs.resource;

import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(
        summary = "Download resource",
        description = "Returns the binary contents of a file or if it’s a directory then a zip archive of contents",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Binary contents of a file or if it’s a directory then a archive of contents",
                content = @Content(mediaType = "application/octet-stream",
                        schema = @Schema(implementation = ResourceResponse.class))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface DownloadResourceDocs {
}
