package com.Uniteams.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Uniteams.Entity.Studygroups;

@Repository
public interface StudygroupsRepo extends JpaRepository<Studygroups, Long> {

    // Encontrar por código
    Optional<Studygroups> findByCode(String code);

    // Encontrar grupos públicos
    List<Studygroups> findByIsPrivateFalse();

    // Encontrar grupos por materia
    List<Studygroups> findBySubjectAndIsPrivateFalse(String subject);

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