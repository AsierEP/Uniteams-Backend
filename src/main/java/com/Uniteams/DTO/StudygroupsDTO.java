package com.Uniteams.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudygroupsDTO {
    private String name;
    private String subject;
    private String sessionType;  // Cambiado a String para mayor flexibilidad
    private LocalDate meetingDate;
    private String meetingDay;   // ✅ NUEVO
    private LocalTime meetingTime;
    private String description;
    private Integer maxParticipants;
    private Boolean isPrivate;
    private String tutorName;
    private String joinLink;
    private String code;         // ✅ NUEVO
    private String createdBy;    // ✅ NUEVO
    private Integer currentParticipants; // ✅ NUEVO

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }

    public String getMeetingDay() { return meetingDay; }
    public void setMeetingDay(String meetingDay) { this.meetingDay = meetingDay; }

    public LocalTime getMeetingTime() { return meetingTime; }
    public void setMeetingTime(LocalTime meetingTime) { this.meetingTime = meetingTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public String getJoinLink() { return joinLink; }
    public void setJoinLink(String joinLink) { this.joinLink = joinLink; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Integer getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(Integer currentParticipants) { this.currentParticipants = currentParticipants; }

    // ✅ MÉTODOS DE UTILIDAD
    public boolean isValidForSessionType() {
        if ("examen".equals(sessionType)) {
            return meetingDate != null && meetingTime != null;
        } else if ("seguimiento".equals(sessionType)) {
            return meetingDay != null && !meetingDay.trim().isEmpty() && meetingTime != null;
        }
        return false;
    }

    public String getScheduleDescription() {
        if ("examen".equals(sessionType)) {
            return meetingDate != null ?
                    "Examen el " + meetingDate.toString() + " a las " + meetingTime.toString() :
                    "Fecha de examen por definir";
        } else if ("seguimiento".equals(sessionType)) {
            return meetingDay != null ?
                    "Todos los " + meetingDay + " a las " + meetingTime.toString() :
                    "Día por definir";
        }
        return "Horario no definido";
    }
}