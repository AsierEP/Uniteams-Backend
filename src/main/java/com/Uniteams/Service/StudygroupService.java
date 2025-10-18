package com.Uniteams.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class StudygroupService {

    private final SupabaseApiService supabaseApiService;

    public StudygroupService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // Obtener todos los grupos públicos
    public List<Map<String, Object>> getPublicStudyGroups() {
        List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
        return allGroups.stream()
                .filter(group -> !Boolean.TRUE.equals(group.get("is_private")))
                .toList();
    }

    // Buscar grupos públicos
    public List<Map<String, Object>> searchPublicGroups(String searchTerm) {
        List<Map<String, Object>> publicGroups = getPublicStudyGroups();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return publicGroups;
        }

        String searchLower = searchTerm.toLowerCase();
        return publicGroups.stream()
                .filter(group ->
                        group.get("name") != null && group.get("name").toString().toLowerCase().contains(searchLower) ||
                                group.get("description") != null && group.get("description").toString().toLowerCase().contains(searchLower) ||
                                group.get("tutor_name") != null && group.get("tutor_name").toString().toLowerCase().contains(searchLower)
                )
                .toList();
    }

    // Obtener grupos por materia
    public List<Map<String, Object>> getGroupsBySubject(String subject) {
        List<Map<String, Object>> publicGroups = getPublicStudyGroups();
        return publicGroups.stream()
                .filter(group -> subject.equalsIgnoreCase(group.get("subject").toString()))
                .toList();
    }

    // Crear nuevo grupo
    public Map<String, Object> createStudyGroup(Map<String, Object> groupData, String userId) {
        // Agregar campos automáticos
        groupData.put("created_by", userId);
        groupData.put("created_at", new Date().toString());
        groupData.put("current_participants", 1);

        // Generar código único
        groupData.put("code", generateUniqueCode());

        return supabaseApiService.createStudyGroup(groupData);
    }

    // Obtener grupos del usuario
    public List<Map<String, Object>> getUserStudyGroups(String userId) {
        List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
        return allGroups.stream()
                .filter(group -> userId.equals(group.get("created_by")))
                .toList();
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}