package com.Uniteams.Service;

import com.Uniteams.Entity.GroupMessage;
import com.Uniteams.Repository.GroupMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMessageService {
    @Autowired
    private GroupMessageRepository groupMessageRepository;

    public List<GroupMessage> getMessagesByGroupId(String groupId) {
        return groupMessageRepository.findByGroupId(groupId);
    }

    public GroupMessage saveMessage(GroupMessage message) {
        return groupMessageRepository.save(message);
    }
}
