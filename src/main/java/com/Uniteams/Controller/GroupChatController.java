package com.Uniteams.Controller;

import com.Uniteams.Entity.GroupMessage;
import com.Uniteams.Service.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-chat")
public class GroupChatController {
    @Autowired
    private GroupMessageService groupMessageService;

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(@PathVariable String groupId) {
        List<GroupMessage> messages = groupMessageService.getMessagesByGroupId(groupId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{groupId}/messages")
    public ResponseEntity<GroupMessage> sendGroupMessage(@PathVariable String groupId, @RequestBody GroupMessage message) {
        message.setGroupId(groupId);
        GroupMessage saved = groupMessageService.saveMessage(message);
        return ResponseEntity.ok(saved);
    }
}
