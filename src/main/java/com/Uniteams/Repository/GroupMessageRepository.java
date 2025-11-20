package com.Uniteams.Repository;

import com.Uniteams.Entity.GroupMessage;
import java.util.List;

public interface GroupMessageRepository {
    List<GroupMessage> findByGroupId(String groupId);
    GroupMessage save(GroupMessage message);
}
