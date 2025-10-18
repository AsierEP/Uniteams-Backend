package com.Uniteams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class
})
public class UniteamsApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniteamsApplication.class, args);
        System.out.println("🚀 Uniteams Backend ejecutándose en http://localhost:8080");
    }
}