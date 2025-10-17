package com.Uniteams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniteamsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniteamsApplication.class, args);
        System.out.println("🚀 Uniteams Backend ejecutándose en http://localhost:8080");
    }
}