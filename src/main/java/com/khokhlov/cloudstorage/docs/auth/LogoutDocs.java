package com.khokhlov.cloudstorage.docs.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Operation(
        summary = "User logout",
        description = "User logout"
)
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "No Content",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = void.class), examples = @ExampleObject(
                        value = """
                                []
                                """
                ))),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface LogoutDocs {
}
