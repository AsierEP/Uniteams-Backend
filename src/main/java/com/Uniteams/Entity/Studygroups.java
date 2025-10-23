package com.Uniteams.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

// ✅ QUITA todas las anotaciones JPA
public class Studygroups {
    private Long id;
    private String code;
    private String joinLink;
    private String name;
    private String subject;
    private SessionType sessionType;
    private LocalDate meetingDate;    // Para exámenes (fecha específica)
    private String meetingDay;        // ✅ NUEVO: Para seguimiento (día de la semana)
    private LocalTime meetingTime;
    private String description;
    private Integer maxParticipants = 10;
    private Integer currentParticipants = 0;
    private Boolean isPrivate = false;
    private String tutorName;
    private String createdBy;
    private LocalDateTime createdAt;
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

    // Getters y Setters (MANTÉN TODOS Y AGREGA meetingDay)
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

    // ✅ NUEVO: Getter y Setter para meetingDay
    public String getMeetingDay() { return meetingDay; }
    public void setMeetingDay(String meetingDay) { this.meetingDay = meetingDay; }

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

    // ✅ QUITA @PreUpdate - no funciona sin JPA
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ MÉTODOS DE UTILIDAD AGREGADOS
    public boolean isValidForSessionType() {
        if (sessionType == SessionType.examen) {
            return meetingDate != null && meetingTime != null;
        } else if (sessionType == SessionType.seguimiento) {
            return meetingDay != null && !meetingDay.trim().isEmpty() && meetingTime != null;
        }
        return false;
    }

    public String getScheduleDescription() {
        if (sessionType == SessionType.examen) {
            return meetingDate != null ?
                    "Examen el " + meetingDate.toString() + " a las " + meetingTime.toString() :
                    "Fecha de examen por definir";
        } else if (sessionType == SessionType.seguimiento) {
            return meetingDay != null ?
                    "Todos los " + meetingDay + " a las " + meetingTime.toString() :
                    "Día por definir";
        }
        return "Horario no definido";
    }

    // ✅ MÉTODO PARA CONVERTIR A MAP (útil para Supabase)
    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("code", code);
        map.put("name", name);
        map.put("subject", subject);
        map.put("session_type", sessionType != null ? sessionType.name() : null);
        map.put("meeting_date", meetingDate);
        map.put("meeting_day", meetingDay);  // ✅ NUEVO
        map.put("meeting_time", meetingTime);
        map.put("description", description);
        map.put("max_participants", maxParticipants);
        map.put("current_participants", currentParticipants);
        map.put("is_private", isPrivate);
        map.put("tutor_name", tutorName);
        map.put("join_link", joinLink);
        map.put("created_by", createdBy);
        return map;
    }
}