package com.Uniteams.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

// POJO alineado con la tabla requests (sin JPA)
public class Requests {
    private Long idRequest;          // id_request (bigint)
    private UUID idUser;             // id_user (uuid)
    private Long idSubject;          // id_subject (bigint)
    private LocalDateTime createdAt; // created_at (timestamptz)
    private Integer grade;           // grade (int8 -> usamos Integer)
    private String carreerName;      // carreer_name (varchar)
    private String description;      // description (text)

    public Requests() {
        this.createdAt = LocalDateTime.now();
    }

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
