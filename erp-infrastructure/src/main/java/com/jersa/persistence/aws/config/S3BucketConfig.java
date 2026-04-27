package com.jersa.persistence.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jersa.persistence.aws.model.RAwsS3Properties;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Slf4j
@Configuration
public class S3BucketConfig {

  @Bean
  public S3Client s3Client(RAwsS3Properties props) {
    log.info("Configuring AWS S3 Bucket");

    var credentials = AwsBasicCredentials.create(props.accessKey(), props.secretKey());

    var s3Config = S3Configuration.builder()
        .pathStyleAccessEnabled(props.pathStyleEnabled())
        .build();

    var s3ClientBuilder = S3Client.builder()
        .region(Region.of(props.region()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .serviceConfiguration(s3Config);

    return s3ClientBuilder.build();
  }
}
