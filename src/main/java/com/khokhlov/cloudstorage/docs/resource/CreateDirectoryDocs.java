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
        summary = "Create directory",
        description = "Creating a directory in the specified location",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResourceResponse.class))),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Parent directory does not exist",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                        value = """
                                {
                                  "message": "Parent folder does not exist"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "409", description = "Directory already exist",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                        value = """
                                {
                                  "message": "Directory already exist"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface CreateDirectoryDocs {
}
