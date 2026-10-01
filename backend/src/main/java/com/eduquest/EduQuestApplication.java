package com.eduquest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.eduquest.domain", "com.eduquest.ai.entity"})
public class EduQuestApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduQuestApplication.class, args);
    }
}
