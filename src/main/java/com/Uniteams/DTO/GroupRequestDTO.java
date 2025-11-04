package com.Uniteams.DTO;

public class GroupRequestDTO {
    private Long id;
    private Long idGroup;
    private Long idTutor;
    private String tutorName;
    private String createdAt;

    public GroupRequestDTO() {
    }

    public GroupRequestDTO(Long id, Long idGroup, Long idTutor, String tutorName, String createdAt) {
        this.id = id;
        this.idGroup = idGroup;
        this.idTutor = idTutor;
        this.tutorName = tutorName;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
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

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "GroupRequestDTO{" +
                "id=" + id +
                ", idGroup=" + idGroup +
                ", idTutor=" + idTutor +
                ", tutorName='" + tutorName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
