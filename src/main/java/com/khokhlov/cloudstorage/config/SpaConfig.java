package com.khokhlov.cloudstorage.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

//@Configuration
//public class SpaConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/static/")
//                .resourceChain(true)
//                .addResolver(new PathResourceResolver() {
//                    @Override
//                    protected Resource getResource(@NotNull String resourcePath, @NotNull Resource location) throws IOException {
//                        if (resourcePath.startsWith("api/")) {
//                            return null;
//                        }
//                        Resource requested = location.createRelative(resourcePath);
//                        if (requested.exists() && requested.isReadable()) {
//                            return requested;
//                        }
//                        return location.createRelative("index.html");
//                    }
//                });
//    }
//}