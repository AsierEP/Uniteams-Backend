package com.Uniteams.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

// POJO alineado con la tabla tutors (sin JPA)
public class Tutors {
    private Long id;                // id (bigint)
    private Long idSubject;         // id_subject (bigint)
    private UUID idUser;            // id_user (uuid)
    private LocalDateTime createdAt;// created_at (timestamptz)

    public Tutors() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdSubject() { return idSubject; }
    public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }

    public UUID getIdUser() { return idUser; }
    public void setIdUser(UUID idUser) { this.idUser = idUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
