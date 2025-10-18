/*package com.Uniteams.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudygroupsRepo {

    private final JdbcTemplate jdbcTemplate;

    public StudygroupsRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        System.out.println("✅ StudygroupsRepo inicializado");
    }

    // ✅ MÉTODO MÍNIMO DE PRUEBA
    public String testConnection() {
        try {
            String sql = "SELECT version()";
            String result = jdbcTemplate.queryForObject(sql, String.class);
            return "✅ Conexión BD exitosa: " + result;
        } catch (Exception e) {
            return "❌ Error en conexión BD: " + e.getMessage();
        }
    }

    // ✅ MÉTODO MÍNIMO DE PRUEBA
    public int countGroups() {
        try {
            String sql = "SELECT COUNT(*) FROM studygroups";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            System.err.println("❌ Error contando grupos: " + e.getMessage());
            return -1;
        }
    }
}*/