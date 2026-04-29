package com.jersa;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ErpApiApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(ErpApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
  }
}
