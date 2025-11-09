package com.khokhlov.cloudstorage.docs.auth;

import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import io.minio.errors.ErrorResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(
        summary = "User registration",
        description = "Registers a user in the application"
)
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AuthRequest.class))),

        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseException.class))),

        @ApiResponse(responseCode = "409", description = "Username already in use",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseException.class))),

        @ApiResponse(responseCode = "500", description = "Unexpected server error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseException.class)))
})
@RequestBody(
        required = true,
        content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthRequest.class),
                examples = @ExampleObject(
                        name = "Registration example",
                        value = """
                                {
                                  "username": "JohnDoe",
                                  "password": "123_Ab.()+-{}@"
                                }
                                """
                )
        )
)
public @interface RegisterUserDocs {
}
