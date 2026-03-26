package com.webtoon.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsS3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(ProfileCredentialsProvider.create(s3Properties.getProfile()))
                .build();
    }
}
