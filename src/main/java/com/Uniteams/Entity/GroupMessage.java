package com.Uniteams.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class GroupMessage {
    private UUID id;
    private String groupId;
    private String senderId;
    private String senderRole;
    private String content;
    private LocalDateTime createdAt;

    public GroupMessage() {}

    public GroupMessage(UUID id, String groupId, String senderId, String senderRole, String content, LocalDateTime createdAt) {
        this.id = id;
        this.groupId = groupId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
