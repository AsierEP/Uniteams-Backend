package com.Uniteams.Service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TutorsService {

    private final SupabaseApiService supabaseApiService;

    public TutorsService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // Crear tutor mapeando camelCase -> snake_case
    public Map<String, Object> createTutor(Map<String, Object> tutorData) {
        Map<String, Object> supabaseData = new HashMap<>();

        if (tutorData.containsKey("id")) {
            supabaseData.put("id", tutorData.get("id"));
        }
        if (tutorData.containsKey("idSubject")) {
            supabaseData.put("id_subject", tutorData.get("idSubject"));
        }
        if (tutorData.containsKey("idUser")) {
            supabaseData.put("id_user", tutorData.get("idUser"));
        }
        if (tutorData.containsKey("createdAt")) {
            supabaseData.put("created_at", tutorData.get("createdAt"));
        }

        System.out.println("📤 Tutor mapeado para Supabase: " + supabaseData);
        return supabaseApiService.createTutor(supabaseData);
    }

    // Listar todos los tutors
    public List<Map<String, Object>> getTutors() {
        try {
            return supabaseApiService.getTutors();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo tutors: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener tutors por usuario
    public List<Map<String, Object>> getTutorsByUser(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) return new ArrayList<>();
            List<Map<String, Object>> all = getTutors();
            return all.stream()
                    .filter(r -> r.get("id_user") != null && userId.equals(r.get("id_user").toString()))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo tutors por usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener tutors por materia
    public List<Map<String, Object>> getTutorsBySubject(Long idSubject) {
        try {
            if (idSubject == null) return new ArrayList<>();
            List<Map<String, Object>> all = getTutors();
            return all.stream()
                    .filter(r -> {
                        Object val = r.get("id_subject");
                        if (val == null) return false;
                        try {
                            return Long.parseLong(val.toString()) == idSubject;
                        } catch (NumberFormatException nfe) {
                            return false;
                        }
                    })
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo tutors por materia: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
