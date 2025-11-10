package com.khokhlov.cloudstorage.docs.resource;

import io.swagger.v3.oas.annotations.Operation;
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
        summary = "Delete resource",
        description = "Returns 204 No Content without body",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface DeleteResourceDocs {
}
