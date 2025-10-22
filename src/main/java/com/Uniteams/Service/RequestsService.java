package com.Uniteams.Service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RequestsService {

    private final SupabaseApiService supabaseApiService;

    public RequestsService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // Crear una request mapeando camelCase -> snake_case
    public Map<String, Object> createRequest(Map<String, Object> requestData) {
        Map<String, Object> supabaseData = new HashMap<>();

        if (requestData.containsKey("idRequest")) {
            supabaseData.put("id_request", requestData.get("idRequest"));
        }
        if (requestData.containsKey("idUser")) {
            supabaseData.put("id_user", requestData.get("idUser"));
        }
        if (requestData.containsKey("idSubject")) {
            supabaseData.put("id_subject", requestData.get("idSubject"));
        }
        if (requestData.containsKey("createdAt")) {
            supabaseData.put("created_at", requestData.get("createdAt"));
        }
        if (requestData.containsKey("grade")) {
            supabaseData.put("grade", requestData.get("grade"));
        }
        if (requestData.containsKey("carreerName")) {
            supabaseData.put("carreer_name", requestData.get("carreerName"));
        }
        if (requestData.containsKey("description")) {
            supabaseData.put("description", requestData.get("description"));
        }

        System.out.println("📤 Request mapeada para Supabase: " + supabaseData);
        return supabaseApiService.createRequest(supabaseData);
    }

    // Listar todas las requests
    public List<Map<String, Object>> getRequests() {
        try {
            return supabaseApiService.getRequests();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Buscar requests por texto (carreer_name o description)
    public List<Map<String, Object>> searchRequests(String searchTerm) {
        try {
            List<Map<String, Object>> all = getRequests();
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return all;
            }
            String s = searchTerm.toLowerCase();
            return all.stream()
                    .filter(row ->
                            (row.get("carreer_name") != null && row.get("carreer_name").toString().toLowerCase().contains(s)) ||
                            (row.get("description") != null && row.get("description").toString().toLowerCase().contains(s))
                    )
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error buscando requests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener requests por usuario
    public List<Map<String, Object>> getRequestsByUser(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) return new ArrayList<>();
            List<Map<String, Object>> all = getRequests();
            return all.stream()
                    .filter(r -> r.get("id_user") != null && userId.equals(r.get("id_user").toString()))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests por usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener requests por materia
    public List<Map<String, Object>> getRequestsBySubject(Long idSubject) {
        try {
            if (idSubject == null) return new ArrayList<>();
            List<Map<String, Object>> all = getRequests();
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
            System.err.println("❌ Error obteniendo requests por materia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Eliminar request por id_request
    public Map<String, Object> deleteRequest(Long id) {
        try {
            boolean deleted = supabaseApiService.deleteRequestById(id);
            if (deleted) {
                return Map.of(
                        "status", "deleted",
                        "id_request", id
                );
            } else {
                return Map.of(
                        "error", "No se pudo eliminar la request",
                        "id_request", id
                );
            }
        } catch (Exception e) {
            System.err.println("❌ Error eliminando request: " + e.getMessage());
            return Map.of(
                    "error", e.getMessage(),
                    "id_request", id
            );
        }
    }
}
