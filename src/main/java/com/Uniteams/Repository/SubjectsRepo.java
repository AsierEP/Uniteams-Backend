package com.Uniteams.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Uniteams.Entity.Subjects;

@Repository
public interface SubjectsRepo extends JpaRepository<Subjects, Long> {

    // Encontrar por nombre exacto
    Optional<Subjects> findByName(String name);

    // Búsqueda por nombre parcial (insensible a mayúsculas)
    @Query("SELECT s FROM Subjects s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Subjects> searchByName(@Param("search") String search);

    // Encontrar por createdAt (timestamptz)
    List<Subjects> findByCreatedAt(OffsetDateTime createdAt);

    // Encontrar por updatedAt (timestamp)
    List<Subjects> findByUpdatedAt(LocalDateTime updatedAt);
}
