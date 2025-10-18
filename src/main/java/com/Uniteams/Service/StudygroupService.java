package com.Uniteams.Service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StudygroupService {

    private final SupabaseApiService supabaseApiService;

    public StudygroupService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // ✅ CORREGIDO: Mapear camelCase a snake_case para Supabase
    public Map<String, Object> createStudyGroup(Map<String, Object> groupData) {
        // Crear nuevo mapa con los nombres de columna correctos para Supabase
        Map<String, Object> supabaseData = new HashMap<>();

        // Mapear campos de camelCase a snake_case
        if (groupData.containsKey("code")) {
            supabaseData.put("code", groupData.get("code"));
        }
        if (groupData.containsKey("name")) {
            supabaseData.put("name", groupData.get("name"));
        }
        if (groupData.containsKey("subject")) {
            supabaseData.put("subject", groupData.get("subject"));
        }
        if (groupData.containsKey("sessionType")) {
            String sessionType = groupData.get("sessionType").toString();
            supabaseData.put("session_type", sessionType.toLowerCase());
            System.out.println("✅ Session Type mapeado: " + sessionType.toLowerCase());
        }
        if (groupData.containsKey("meetingDate")) {
            supabaseData.put("meeting_date", groupData.get("meetingDate"));
        }
        if (groupData.containsKey("meetingTime")) {
            supabaseData.put("meeting_time", groupData.get("meetingTime"));
        }
        if (groupData.containsKey("description")) {
            supabaseData.put("description", groupData.get("description"));
        }
        if (groupData.containsKey("maxParticipants")) {
            supabaseData.put("max_participants", groupData.get("maxParticipants"));
        }
        if (groupData.containsKey("currentParticipants")) {
            supabaseData.put("current_participants", groupData.get("currentParticipants"));
        }
        if (groupData.containsKey("isPrivate")) {
            supabaseData.put("is_private", groupData.get("isPrivate"));
        }
        if (groupData.containsKey("tutorName")) {
            supabaseData.put("tutor_name", groupData.get("tutorName"));
        }
        if (groupData.containsKey("joinLink")) {
            supabaseData.put("join_link", groupData.get("joinLink"));
        }
        // ✅ CAMPO CORREGIDO: createdBy -> created_by
        if (groupData.containsKey("createdBy")) {
            supabaseData.put("created_by", groupData.get("createdBy"));
        }

        System.out.println("📤 Datos mapeados para Supabase: " + supabaseData);

        return supabaseApiService.createStudyGroup(supabaseData);
    }

    // Obtener todos los grupos públicos
    public List<Map<String, Object>> getPublicStudyGroups() {
        try {
            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group -> !Boolean.TRUE.equals(group.get("is_private")))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos públicos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Buscar grupos públicos
    public List<Map<String, Object>> searchPublicGroups(String searchTerm) {
        try {
            List<Map<String, Object>> publicGroups = getPublicStudyGroups();

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return publicGroups;
            }

            String searchLower = searchTerm.toLowerCase();
            return publicGroups.stream()
                    .filter(group ->
                            (group.get("name") != null && group.get("name").toString().toLowerCase().contains(searchLower)) ||
                                    (group.get("description") != null && group.get("description").toString().toLowerCase().contains(searchLower)) ||
                                    (group.get("tutor_name") != null && group.get("tutor_name").toString().toLowerCase().contains(searchLower)) ||
                                    (group.get("subject") != null && group.get("subject").toString().toLowerCase().contains(searchLower))
                    )
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error buscando grupos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener grupos por materia
    public List<Map<String, Object>> getGroupsBySubject(String subject) {
        try {
            List<Map<String, Object>> publicGroups = getPublicStudyGroups();
            return publicGroups.stream()
                    .filter(group ->
                            subject != null &&
                                    group.get("subject") != null &&
                                    subject.equalsIgnoreCase(group.get("subject").toString()))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos por materia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener grupos del usuario
    public List<Map<String, Object>> getUserStudyGroups(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group ->
                            group.get("created_by") != null &&
                                    userId.equals(group.get("created_by").toString()))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos del usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener grupo por código
    public Map<String, Object> getStudyGroupByCode(String code) {
        try {
            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group ->
                            code != null &&
                                    group.get("code") != null &&
                                    code.equals(group.get("code").toString()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupo por código: " + e.getMessage());
            return null;
        }
    }

    // ✅ NUEVO: Actualizar participantes
    public Map<String, Object> updateParticipants(String code, int newParticipantCount) {
        try {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("current_participants", newParticipantCount);

            // Aquí necesitarías implementar un método update en SupabaseApiService
            // Por ahora, obtenemos el grupo y lo actualizamos manualmente
            Map<String, Object> group = getStudyGroupByCode(code);
            if (group != null) {
                group.put("current_participants", newParticipantCount);
                return group;
            }
            return Map.of("error", "Grupo no encontrado");
        } catch (Exception e) {
            System.err.println("❌ Error actualizando participantes: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ NUEVO: Obtener materias disponibles
    public List<String> getAvailableSubjects() {
        try {
            List<Map<String, Object>> publicGroups = getPublicStudyGroups();
            return publicGroups.stream()
                    .filter(group -> group.get("subject") != null)
                    .map(group -> group.get("subject").toString())
                    .distinct()
                    .sorted()
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo materias disponibles: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}