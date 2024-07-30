package com.learnonline.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.media.config
 * @Author: ASUS
 * @CreateTime: 2024-07-29  22:42
 * @Description: minio配置minio配置
 * @Version: 1.0
 */
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")//读取配置信息
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }

}
