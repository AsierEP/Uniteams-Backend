package com.Uniteams.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "studygroups")
public class Studygroups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 8)
    private String code;

    @Column(name = "join_link", length = 500)
    private String joinLink;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private SessionType sessionType;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "meeting_time", nullable = false)
    private LocalTime meetingTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants = 10;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants = 0;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @Column(name = "tutor_name", length = 100)
    private String tutorName;

    @Column(name = "created_by", nullable = false)
    private String createdBy; // UUID del usuario

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum SessionType {
        seguimiento, examen
    }

    // Constructores
    public Studygroups() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters (omitiendo por brevedad, pero necesarios)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getJoinLink() { return joinLink; }
    public void setJoinLink(String joinLink) { this.joinLink = joinLink; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public SessionType getSessionType() { return sessionType; }
    public void setSessionType(SessionType sessionType) { this.sessionType = sessionType; }

    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }

    public LocalTime getMeetingTime() { return meetingTime; }
    public void setMeetingTime(LocalTime meetingTime) { this.meetingTime = meetingTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}