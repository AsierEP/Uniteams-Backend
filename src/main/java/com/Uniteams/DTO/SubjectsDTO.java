package com.Uniteams.DTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class SubjectsDTO {
    private Long idSubject;
    private String name;
    private OffsetDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getIdSubject() { return idSubject; }
    public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
