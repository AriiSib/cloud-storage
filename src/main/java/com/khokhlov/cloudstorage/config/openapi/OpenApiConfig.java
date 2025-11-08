package com.khokhlov.cloudstorage.config.openapi;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.HashSet;
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

        AnnotatedElementUtils.findAllMergedAnnotations(hm.getMethod(), ApiResponse.class)
                .forEach(a -> codes.add(a.responseCode()));

        AnnotatedElementUtils.findAllMergedAnnotations(hm.getBeanType(), ApiResponse.class)
                .forEach(a -> codes.add(a.responseCode()));

        return codes;
    }
}