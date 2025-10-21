package com.Uniteams.DTO;

import java.time.LocalDateTime;

public class SubjectsDTO {
	// Alineado con la tabla subjects: id_subject, name, created_at, updated_at
	private Long idSubject;        // id_subject (bigint)
	private String name;              // name (varchar)
	private LocalDateTime createdAt;  // created_at (timestamptz)
	private LocalDateTime updatedAt;  // updated_at (timestamp)

	// Getters y Setters
    public Long getIdSubject() { return idSubject; }
    public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

