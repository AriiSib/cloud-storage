package com.khokhlov.cloudstorage.docs.auth;

import com.khokhlov.cloudstorage.model.dto.request.AuthRequest;
import com.khokhlov.cloudstorage.model.dto.response.ErrorResponse;
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
                        schema = @Schema(implementation = AuthRequest.class), examples = @ExampleObject(
                        value = """
                                {
                                  "username": "JohnDoe"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "400", description = "Validation error",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                        value = """
                                {
                                  "message": "Size should be in the range from 4 to 20"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "409", description = "Username already in use",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(
                        value = """
                                {
                                  "message": "Username already in use: JohnDoe"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
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
public @interface RegisterDocs {
}
