package com.enterprise.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@Configuration
@EnableScheduling
public class EnterpriseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseBackendApplication.class, args);
    }

}
