package com.Uniteams.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class SupabaseApiService {

    private final String supabaseUrl = "https://zskuikxfcjobpygoueqp.supabase.co/rest/v1";

    // ✅ REEMPLAZA con tu SERVICE_ROLE KEY (debe empezar con eyJhb...)
    private final String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inpza3Vpa3hmY2pvYnB5Z291ZXFwIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc1OTQ0MzE0NCwiZXhwIjoyMDc1MDE5MTQ0fQ.Q25utwwQYJ5zful7utYTK3JT2zkitGtoxOzCkGlDKiQ";

    private final RestTemplate restTemplate;

    public SupabaseApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=representation");
        return headers;
    }

    // Obtener todos los grupos
    public List<Map<String, Object>> getStudyGroups() {
        try {
            String url = supabaseUrl + "/studygroups?select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            // ✅ Esto ya está correcto - Supabase devuelve array para múltiples registros
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos: " + e.getMessage());
            e.printStackTrace(); // ✅ Agregar stack trace
            return new ArrayList<>();
        }
    }

    // ✅ CORREGIDO: Crear grupo y devolver el ID real
    public Map<String, Object> createStudyGroup(Map<String, Object> groupData) {
        try {
            String url = supabaseUrl + "/studygroups?select=id"; // <-- Pedimos que devuelva el ID

            // Asegúrate de que los headers pidan la representación
            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=representation,resolution=merge-duplicates");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(groupData, headers);

            System.out.println("🚀 Enviando a Supabase...");

            // Pedimos un Map[] porque Supabase devuelve un array con el objeto creado
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map[].class);

            // Si Supabase devuelve un cuerpo y un array no vacío, la creación fue exitosa
            if (response.getBody() != null && response.getBody().length > 0) {
                System.out.println("✅ Grupo creado exitosamente en Supabase.");
                // Devolvemos el primer objeto del array, que es nuestro grupo con su ID
                return response.getBody()[0];
            }

            // Si no, devolvemos un error
            System.err.println("❌ Supabase devolvió OK pero sin cuerpo de respuesta.");
            return Map.of("error", "Supabase devolvió OK pero sin cuerpo de respuesta.");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // ✅ ESTE ES EL CATCH MÁS IMPORTANTE
            // Captura errores 4xx y 5xx y muestra el error REAL de Supabase
            System.err.println("❌ Error REAL de Supabase: " + e.getResponseBodyAsString());
            return Map.of("error", "Error de Supabase: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // Captura genérica para otros errores (red, etc.)
            System.err.println("❌ Error creando grupo: " + e.getMessage());
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ NUEVO: Obtener perfil del usuario
    public Map<String, Object> getUserProfile(String userId) {
        try {
            String url = supabaseUrl + "/profiles?id=eq." + userId + "&select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] profiles = response.getBody();
            if (profiles != null && profiles.length > 0) {
                return profiles[0];
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo perfil del usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ✅ NUEVO: Actualizar perfil del usuario
    public boolean updateUserProfile(String userId, Map<String, Object> updateData) {
        try {
            String url = supabaseUrl + "/profiles?id=eq." + userId;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updateData, createHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, String.class);

            System.out.println("✅ Perfil actualizado exitosamente para usuario: " + userId);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error actualizando perfil del usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NUEVO: Actualizar grupo de estudio
    public boolean updateStudyGroup(String groupId, Map<String, Object> updateData) {
        try {
            String url = supabaseUrl + "/studygroups?id=eq." + groupId;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(updateData, createHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, String.class);

            System.out.println("✅ Grupo actualizado exitosamente: " + groupId);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error actualizando grupo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NUEVO: Obtener grupo por ID
    public Map<String, Object> getStudyGroupById(String groupId) {
        try {
            String url = supabaseUrl + "/studygroups?id=eq." + groupId + "&select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] groups = response.getBody();
            if (groups != null && groups.length > 0) {
                return groups[0];
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupo por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ✅ NUEVO: Obtener múltiples grupos por IDs
    public List<Map<String, Object>> getStudyGroupsByIds(List<String> groupIds) {
        try {
            if (groupIds == null || groupIds.isEmpty()) {
                return new ArrayList<>();
            }

            // Crear filtro IN para múltiples IDs
            String idsFilter = String.join(",", groupIds);
            String url = supabaseUrl + "/studygroups?id=in.(" + idsFilter + ")&select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] groups = response.getBody();
            if (groups != null && groups.length > 0) {
                return Arrays.asList(groups);
            }
            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos por IDs: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Verificar si usuario está en grupo
    public boolean isUserInGroup(String userId, String groupId) {
        try {
            Map<String, Object> profile = getUserProfile(userId);
            if (profile != null && profile.containsKey("study_groups")) {
                Object studyGroups = profile.get("study_groups");
                if (studyGroups instanceof List) {
                    return ((List<?>) studyGroups).contains(groupId);
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error verificando membresía de grupo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NUEVO: Obtener todos los perfiles (útil para debugging)
    public List<Map<String, Object>> getAllProfiles() {
        try {
            String url = supabaseUrl + "/profiles?select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] profiles = response.getBody();
            if (profiles != null && profiles.length > 0) {
                return Arrays.asList(profiles);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo perfiles: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}