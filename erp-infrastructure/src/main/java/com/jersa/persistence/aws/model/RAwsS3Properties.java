package com.jersa.persistence.aws.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "aws.s3")
public record RAwsS3Properties(
    @NotBlank(message = "El endpoint de S3 no puede estar vacío") String endpoint,

    @NotBlank(message = "La región de S3 no puede estar vacía") String region,

    @NotBlank(message = "El access-key de S3 no puede estar vacío") String accessKey,

    @NotBlank(message = "El secret-key de S3 no puede estar vacío") String secretKey,

    @NotBlank(message = "El bucket-name de S3 no puede estar vacío") String bucketName,

    Boolean pathStyleEnabled) {
  public String getBucketUrl() {
    return String.format("%s/%s", endpoint, bucketName);
  }
}
