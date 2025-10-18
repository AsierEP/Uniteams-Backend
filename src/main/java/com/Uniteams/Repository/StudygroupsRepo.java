/*package com.Uniteams.Repository;

<<<<<<< HEAD
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Uniteams.Entity.Studygroups;

=======
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

>>>>>>> master
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

<<<<<<< HEAD
    // Encontrar grupos por tipo de sesión
    List<Studygroups> findBySessionTypeAndIsPrivateFalse(Studygroups.SessionType sessionType);

    // Encontrar grupos por fecha
    List<Studygroups> findByMeetingDateAndIsPrivateFalse(LocalDate meetingDate);

    // Búsqueda por nombre, descripción o tutor
    @Query("SELECT sg FROM Studygroups sg WHERE sg.isPrivate = false AND " +
        "(LOWER(sg.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(sg.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(sg.tutorName) LIKE LOWER(CONCAT('%', :search, '%')))" )
    List<Studygroups> searchPublicGroups(@Param("search") String search);

    // Encontrar grupos de un usuario
    List<Studygroups> findByCreatedBy(String createdBy);
}
=======
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
>>>>>>> master
