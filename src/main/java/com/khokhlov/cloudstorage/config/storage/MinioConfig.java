package com.khokhlov.cloudstorage.config.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public CommandLineRunner ensureBucket(MinioClient minio) {
        return args -> {
            boolean exists = minio.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build());
            if (!exists) {
                minio.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build());
            }
        };
    }
}
