package com.hikers.hikemate.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Bean
    public AmazonS3 amazonS3() {
        // AWS SDK 1.x에서 사용하는 BasicAWSCredentials
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

        // AmazonS3ClientBuilder를 사용하여 AWS S3 클라이언트 생성
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)  // 지역을 설정
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))  // 인증 정보 설정
                .build();  // 클라이언트 빌드
    }
}
