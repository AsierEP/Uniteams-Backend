package com.Uniteams.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class RequestsDTO {
    private Long idRequest;          // id_request
    private UUID idUser;             // id_user
    private Long idSubject;          // id_subject
    private LocalDateTime createdAt; // created_at
    private Integer grade;           // grade
    private String carreerName;      // carreer_name
    private String description;      // description

    public Long getIdRequest() { return idRequest; }
    public void setIdRequest(Long idRequest) { this.idRequest = idRequest; }

    public UUID getIdUser() { return idUser; }
    public void setIdUser(UUID idUser) { this.idUser = idUser; }

    public Long getIdSubject() { return idSubject; }
    public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public String getCarreerName() { return carreerName; }
    public void setCarreerName(String carreerName) { this.carreerName = carreerName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
