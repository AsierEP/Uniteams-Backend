package com.Uniteams.Entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "subjects")
public class Subjects {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_subject")
	private Long idSubject;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	// Supabase created_at is timestamptz (timestamp with time zone)
	@Column(name = "created_at", updatable = false)
	private OffsetDateTime createdAt;

	// Supabase updated_at is timestamp (without timezone)
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Subjects() {
		this.createdAt = OffsetDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public Long getIdSubject() { return idSubject; }
	public void setIdSubject(Long idSubject) { this.idSubject = idSubject; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public OffsetDateTime getCreatedAt() { return createdAt; }
	public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}

