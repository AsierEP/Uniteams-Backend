package com.Uniteams.DTO;

import com.Uniteams.Entity.Studygroups.SessionType;
import java.time.LocalDate;
import java.time.LocalTime;

public class StudygroupsDTO {
    private String name;
    private String subject;
    private SessionType sessionType;
    private LocalDate meetingDate;
    private LocalTime meetingTime;
    private String description;
    private Integer maxParticipants;
    private Boolean isPrivate;
    private String tutorName;
    private String joinLink;

    // Getters y Setters
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

    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public String getJoinLink() { return joinLink; }
    public void setJoinLink(String joinLink) { this.joinLink = joinLink; }
}