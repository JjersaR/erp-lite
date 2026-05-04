package com.jersa.ports.services;

import com.jersa.entities.product.RProductImage;

public interface IImageStorageServicePort {

  RProductImage upload(String imageName, byte[] imageData);

  void delete(RProductImage image);

  byte[] download(RProductImage image);

}
