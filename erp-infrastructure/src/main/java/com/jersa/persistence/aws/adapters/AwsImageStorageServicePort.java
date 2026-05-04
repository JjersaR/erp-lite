package com.jersa.persistence.aws.adapters;

import com.jersa.exceptions.MyBussinessException;
import org.springframework.stereotype.Service;

import com.jersa.persistence.aws.model.RAwsS3Properties;
import com.jersa.ports.services.IImageStorageServicePort;
import com.jersa.entities.product.RProductImage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsImageStorageServicePort implements IImageStorageServicePort {

  private final S3Client s3Client;
  private final RAwsS3Properties properties;

  @Override
  public void delete(RProductImage image) {
    try {
      final var key = this.getKeyFromUrl(image.imageUrl());
      final var deleteObjectRequest = DeleteObjectRequest.builder().bucket(properties.bucketName()).key(key).build();

      this.s3Client.deleteObject(deleteObjectRequest);
      log.info("Deleted image success: {}", image.imageUrl());


    } catch (S3Exception s3e) {
      log.error("S3Exception", s3e);
      throw new MyBussinessException("Error deleting image: " + s3e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error deleting image", e);
      throw new MyBussinessException("Error deleting image: " + e.getMessage());
    }
  }

  @Override
  public byte[] download(RProductImage image) {
    try {
      final var key = getKeyFromUrl(image.imageUrl());
      final var getObjectRequest = GetObjectRequest.builder().bucket(properties.bucketName()).key(key).build();
      final var bytes = this.s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

      log.info("Downloaded image {} bytes", bytes.length);

      return bytes;

    } catch (S3Exception s3e) {
      log.error("S3Exception", s3e);
      throw new MyBussinessException("Error downloading image: " + s3e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error downloading image", e);
      throw new MyBussinessException("Error downloading image: " + e.getMessage());
    }
  }

  @Override
  public RProductImage upload(String imageName, byte[] imageData) {
    try {
      final var key ="products/" + imageName;
      final var putObjectRequest = PutObjectRequest.builder().bucket(properties.bucketName()).key(key).contentType(this.determineContentType(imageName)).contentLength((long) imageData.length).build();
      this.s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageData));
      log.info("Image uploaded successfully");
      return new RProductImage(this.buildUrlImg(key));
    } catch (S3Exception s3e) {
      log.error("S3Exception", s3e);
      throw new MyBussinessException("Error uploading image: " + s3e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error uploading image", e);
      throw new MyBussinessException("Error uploading image: " + e.getMessage());
    }
  }

  /**
   * @param url https://amazonaws/erp-products/products/mac-01.png
   * @return products/mac-01.png
   */
  private String getKeyFromUrl(String url) {
    var bucketName = properties.bucketName();
    var parts = url.split("/" + bucketName + "/");

    if (parts.length > 1) {
      return parts[1];
    }

    log.warn("No bucket name found in url {}", url);
    return url;
  }

  private String buildUrlImg(String key) {
    final var placeholder = "%s/%s/%s";
    return String.format(placeholder, this.properties.endpoint(), this.properties.bucketName(), key);
  }

  private String determineContentType(String filename) {
    final var extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

    return switch (extension) {
      case "jpg" -> "image/jpeg";
      case "png" -> "image/png";
      case "webp" -> "image/webp";
      default -> "application/octet-stream";
    };
  }
}
