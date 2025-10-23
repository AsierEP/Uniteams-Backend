package com.Uniteams.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class RequestsService {

    private final SupabaseApiService supabaseApiService;

    public RequestsService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // Crear una request mapeando camelCase -> snake_case
    public Map<String, Object> createRequest(Map<String, Object> requestData) {
        Map<String, Object> supabaseData = new HashMap<>();

        // (Tu mapeo está bien)
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
    // ADVERTENCIA: Esto sigue siendo ineficiente.
    // Una mejor solución usaría un filtro 'like' en SupabaseApiService.
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

    // ✅ CORREGIDO: Obtener requests por usuario (versión eficiente)
    public List<Map<String, Object>> getRequestsByUser(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) return new ArrayList<>();

            // Llama al nuevo método que filtra en la base de datos
            return supabaseApiService.getRequestsByUserId(userId);

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests por usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ✅ CORREGIDO: Obtener requests por materia (versión eficiente)
    public List<Map<String, Object>> getRequestsBySubject(Long idSubject) {
        try {
            if (idSubject == null) return new ArrayList<>();

            // Llama al nuevo método que filtra en la base de datos
            return supabaseApiService.getRequestsBySubjectId(idSubject);

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests por materia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ✅ CORREGIDO: Eliminar request por id_request
    public Map<String, Object> deleteRequest(Long id) {
        try {
            // CORRECCIÓN: Convertir el Long a String antes de pasarlo a la API
            boolean deleted = supabaseApiService.deleteRequestById(id.toString());

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