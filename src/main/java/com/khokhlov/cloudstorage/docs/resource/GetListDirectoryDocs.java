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
        summary = "Information about the resources in directory",
        description = "Returns a list of resources in the specified directory",
        security = @SecurityRequirement(name = "cookieAuth")
)
@ApiResponses({
        @ApiResponse(responseCode = "200", ref = "#/components/responses/OK"),
        @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
        @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
        @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
        @ApiResponse(responseCode = "500", ref = "#/components/responses/ServerError")
})
public @interface GetListDirectoryDocs {
}
