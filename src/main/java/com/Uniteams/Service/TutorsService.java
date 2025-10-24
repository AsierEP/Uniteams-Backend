package com.Uniteams.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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

    // ✅ NUEVO: Crear tutor desde una request y actualizar estado a ACEPTADO
    public Map<String, Object> createTutorFromRequest(Long idRequest) {
        try {
            if (idRequest == null) {
                return Map.of("error", "El id_request es obligatorio");
            }

            // 1) Obtener la request
            Map<String, Object> req = supabaseApiService.getRequestById(idRequest.toString());
            if (req == null) {
                return Map.of("error", "Request no encontrada", "id_request", idRequest);
            }

            Object idUser = req.get("id_user");
            Object idSubject = req.get("id_subject");
            if (idUser == null || idSubject == null) {
                return Map.of("error", "La request no tiene id_user o id_subject", "id_request", idRequest);
            }

            // 2) Crear tutor con esos datos
            Map<String, Object> tutorPayload = new HashMap<>();
            tutorPayload.put("id_user", idUser);
            tutorPayload.put("id_subject", idSubject);

            Map<String, Object> createdTutor = supabaseApiService.createTutor(tutorPayload);
            if (createdTutor.containsKey("error")) {
                return Map.of(
                        "error", "No se pudo crear el tutor",
                        "detalle", createdTutor.get("error"),
                        "id_request", idRequest
                );
            }

            // 3) Actualizar estado de la request a ACEPTADO
            Map<String, Object> updateResult = supabaseApiService.updateRequestState(idRequest.toString(), "ACEPTADO");
            if (updateResult.containsKey("error")) {
                return Map.of(
                        "error", "Tutor creado pero falló actualizar estado de request",
                        "detalle", updateResult.get("error"),
                        "tutor", createdTutor,
                        "id_request", idRequest
                );
            }

            // 4) Devolver resultado combinado
            return Map.of(
                    "status", "ok",
                    "tutor", createdTutor,
                    "request_state", updateResult
            );

        } catch (Exception e) {
            System.err.println("❌ Error creando tutor desde request: " + e.getMessage());
            return Map.of("error", e.getMessage(), "id_request", idRequest);
        }
    }
}
