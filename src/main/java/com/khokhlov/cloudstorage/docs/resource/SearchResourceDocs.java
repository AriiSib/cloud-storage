package com.khokhlov.cloudstorage.docs.resource;

import com.khokhlov.cloudstorage.model.dto.request.ResourceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
        summary = "Search resource",
        description = "Returns a list of resources found for a given query or an empty array if nothing is found",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResourceRequest.class), examples = @ExampleObject(
                        value = """
                                [
                                    {
                                        "path": "folder/file.txt",
                                        "name": "file.txt",
                                        "size": 123,
                                        "type": "FILE"
                                    }
                                ]
                                """
                ))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface SearchResourceDocs {
}
