package com.jersa.ports;

import com.jersa.product.RProductImage;

public interface IImageStorageService {

  RProductImage upload(String imageName, byte[] imageData);

  void delete(RProductImage image);

  byte[] download(RProductImage image);

}
