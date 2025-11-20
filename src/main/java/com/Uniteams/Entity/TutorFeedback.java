package com.Uniteams.Entity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TutorFeedback {
    private Long id;                // id
    private Long tutorId;           // tutor_id
    private UUID studentUserId;     // student_user_id
    private Long studygroupId;      // studygroup_id
    private String comment;         // comment
    private Integer stars;          // stars (1-5)
    private OffsetDateTime createdAt; // created_at
    private OffsetDateTime updatedAt; // updated_at

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }

    public UUID getStudentUserId() { return studentUserId; }
    public void setStudentUserId(UUID studentUserId) { this.studentUserId = studentUserId; }

    public Long getStudygroupId() { return studygroupId; }
    public void setStudygroupId(Long studygroupId) { this.studygroupId = studygroupId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
