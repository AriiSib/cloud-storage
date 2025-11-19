package com.khokhlov.cloudstorage.docs.resource;

import com.khokhlov.cloudstorage.model.dto.response.ErrorResponse;
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
        summary = "Move or rename resource",
        description = "Returns the new path of a moved or renamed resource",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "200", ref = "#/components/responses/OK"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
        @ApiResponse(responseCode = "409", description = "Resource located on the path 'to' already exists",
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
public @interface MoveResourceDocs {
}
