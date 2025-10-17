package com.Uniteams.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseTest implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            String result = jdbcTemplate.queryForObject(
                    "SELECT version()", String.class);
            System.out.println("✅ Conexión a BD exitosa: " + result);
        } catch (Exception e) {
            System.err.println("❌ Error conectando a BD: " + e.getMessage());
        }
    }
}