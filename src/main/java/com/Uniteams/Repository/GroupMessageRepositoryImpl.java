package com.Uniteams.Repository;

import com.Uniteams.Entity.GroupMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Repository
public class GroupMessageRepositoryImpl implements GroupMessageRepository {
    @Autowired
    private RestTemplate restTemplate;

    private final String SUPABASE_URL = "https://zskuikxfcjobpygoueqp.supabase.co/rest/v1/group_messages";
    private final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inpza3Vpa3hmY2pvYnB5Z291ZXFwIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1OTQ0MzE0NCwiZXhwIjoyMDc1MDE5MTQ0fQ.Q25utwwQYJ5zful7utYTK3JT2zkitGtoxOzCkGlDKiQ";

    @Override
    public List<GroupMessage> findByGroupId(String groupId) {
        String url = SUPABASE_URL + "?group_id=eq." + groupId + "&order=created_at.asc";
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", SUPABASE_API_KEY);
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GroupMessage[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, GroupMessage[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    @Override
    public GroupMessage save(GroupMessage message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", SUPABASE_API_KEY);
        headers.set("Authorization", "Bearer " + SUPABASE_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GroupMessage> entity = new HttpEntity<>(message, headers);
        ResponseEntity<GroupMessage[]> response = restTemplate.exchange(SUPABASE_URL, HttpMethod.POST, entity, GroupMessage[].class);
        return Objects.requireNonNull(response.getBody())[0];
    }
}
