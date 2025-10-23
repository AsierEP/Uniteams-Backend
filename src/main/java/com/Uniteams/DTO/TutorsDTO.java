package com.Uniteams.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class TutorsDTO {
    private Long id;                // id
    private Long idSubject;         // id_subject
    private UUID idUser;            // id_user
    private LocalDateTime createdAt;// created_at

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdSubject() { return idSubject; }
    public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }

    public UUID getIdUser() { return idUser; }
    public void setIdUser(UUID idUser) { this.idUser = idUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
