package com.jersa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@Slf4j
@SpringBootApplication
public class ErpApiApplication implements CommandLineRunner {

    static void main(String[] args) {
        SpringApplication.run(ErpApiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
