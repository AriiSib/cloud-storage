package com.khokhlov.cloudstorage.docs.resource;

import com.khokhlov.cloudstorage.model.dto.response.ErrorResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
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
        summary = "Upload resource",
        description = "Returns a list of loaded resources",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "List of uploaded resources",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResourceResponse.class))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "409", description = "Resource already exists",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                        value = """
                                {
                                  "message": "Resource already exists"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface UploadResourceDocs {
}
