package com.khokhlov.cloudstorage.config.openapi;

import com.khokhlov.cloudstorage.model.dto.response.ErrorResponse;
import com.khokhlov.cloudstorage.model.dto.response.ResourceResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.COOKIE;


@SecurityScheme(
        name = "cookieAuth",
        type = SecuritySchemeType.APIKEY,
        in = COOKIE,
        paramName = "SESSION"
)
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cloud Storage API")
                        .description("The API is designed to interact with the Cloud Storage application")
                        .version("1.0"));
    }

    @Bean
    public OpenAPI openAPI() {
        Components components = new Components();

        ModelConverters.getInstance().read(ErrorResponse.class)
                .forEach(components::addSchemas);
        ModelConverters.getInstance().read(ResourceResponse.class)
                .forEach(components::addSchemas);

        Schema<?> errorRef = new Schema<>().$ref("#/components/schemas/ErrorResponse");
        Schema<?> resourceRef = new Schema<>().$ref("#/components/schemas/ResourceResponse");

        components.addResponses("OK",
                new ApiResponse()
                        .description("OK")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().schema(resourceRef))));

        components.addResponses("BadRequest",
                new ApiResponse()
                        .description("Invalid or missing path")
                        .content(new Content().addMediaType("application/json",
                                new MediaType()
                                        .schema(errorRef)
                                        .addExamples("default",
                                                new Example().value(Map.of("message", "Invalid or missing path"))))));

        components.addResponses("Unauthorized",
                new ApiResponse()
                        .description("Unauthorized")
                        .content(new Content().addMediaType("application/json",
                                new MediaType()
                                        .schema(errorRef)
                                        .addExamples("default",
                                                new Example().value(Map.of("message", "User is unauthorized"))))));

        components.addResponses("NotFound",
                new ApiResponse()
                        .description("Resource not found")
                        .content(new Content().addMediaType("application/json",
                                new MediaType()
                                        .schema(errorRef)
                                        .addExamples("default",
                                                new Example().value(Map.of("message", "File not found: /folder/file.txt"))))));

        components.addResponses("ServerError",
                new ApiResponse()
                        .description("Unexpected server error")
                        .content(new Content().addMediaType("application/json",
                                new MediaType()
                                        .schema(errorRef)
                                        .addExamples("default",
                                                new Example().value(Map.of("message", "Internal server error"))))));

        return new OpenAPI().components(components);
    }

    @Bean
    public OperationCustomizer keepOnlyDeclaredResponses() {
        return (operation, handlerMethod) -> {
            Set<String> declared = collectDeclaredResponseCodes(handlerMethod);

            ApiResponses responses = operation.getResponses();
            if (responses != null && !declared.isEmpty()) {
                responses.keySet().removeIf(code -> !declared.contains(code));
            }
            return operation;
        };
    }

    private static Set<String> collectDeclaredResponseCodes(HandlerMethod hm) {
        Set<String> codes = new HashSet<>();

        AnnotatedElementUtils.findAllMergedAnnotations(hm.getMethod(), io.swagger.v3.oas.annotations.responses.ApiResponse.class)
                .forEach(a -> codes.add(a.responseCode()));

        AnnotatedElementUtils.findAllMergedAnnotations(hm.getBeanType(), io.swagger.v3.oas.annotations.responses.ApiResponse.class)
                .forEach(a -> codes.add(a.responseCode()));

        return codes;
    }

}