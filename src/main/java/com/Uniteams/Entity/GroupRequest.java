package com.Uniteams.Entity;

import java.time.LocalDateTime;

public class GroupRequest {
    private Long id;
    private Long idGroup;
    private Long idTutor;
    private String tutorName;
    private LocalDateTime createdAt;

    public GroupRequest() {
        this.createdAt = LocalDateTime.now();
    }

    public GroupRequest(Long id, Long idGroup, Long idTutor, String tutorName, LocalDateTime createdAt) {
        this.id = id;
        this.idGroup = idGroup;
        this.idTutor = idTutor;
        this.tutorName = tutorName;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public Long getIdTutor() {
        return idTutor;
    }

    public String getTutorName() {
        return tutorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public void setIdTutor(Long idTutor) {
        this.idTutor = idTutor;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "GroupRequest{" +
                "id=" + id +
                ", idGroup=" + idGroup +
                ", idTutor=" + idTutor +
                ", tutorName='" + tutorName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
