package com.Uniteams.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    // ✅ NUEVO: Obtener grupos sin tutor asignado (tutor_id IS NULL) y ordenar por created_at DESC
    public List<Map<String, Object>> getStudyGroupsWithoutTutor() {
        try {
            String url = supabaseUrl + "/studygroups?tutor_id=is.null&select=*&order=created_at.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] groups = response.getBody();
            if (groups != null && groups.length > 0) {
                return Arrays.asList(groups);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos sin tutor: " + e.getMessage());
            e.printStackTrace();
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

    // ✅ NUEVO: Obtener grupo por ID numérico
    public Map<String, Object> getStudyGroupByIdNum(Long groupId) {
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
            System.err.println("❌ Error obteniendo grupo por ID numérico: " + e.getMessage());
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

    public Map<String, Object> createSubject(Map<String, Object> subjectData) {
        try {
            // ASUME que tu tabla en Supabase se llama 'subjects' (en plural)
            String url = supabaseUrl + "/subjects?select=*"; // <-- CAMBIO DE TABLA

            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=representation,resolution=merge-duplicates");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(subjectData, headers);

            System.out.println("🚀 Creando nueva materia en Supabase...");

            // Pedimos un Map[] porque Supabase devuelve un array con el objeto creado
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map[].class);

            // Si Supabase devuelve un cuerpo y un array no vacío, la creación fue exitosa
            if (response.getBody() != null && response.getBody().length > 0) {
                System.out.println("✅ Materia creada exitosamente.");
                // Devolvemos el primer objeto del array, que es nuestro grupo con su ID
                return response.getBody()[0];
            }

            // Si no, devolvemos un error
            System.err.println("❌ Supabase devolvió OK pero sin cuerpo de respuesta.");
            return Map.of("error", "Supabase devolvió OK pero sin cuerpo de respuesta.");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // ✅ ESTE ES EL CATCH MÁS IMPORTANTE
            // Captura errores 4xx y 5xx y muestra el error REAL de Supabase
            System.err.println("❌ Error REAL de Supabase al crear materia: " + e.getResponseBodyAsString());
            return Map.of("error", "Error de Supabase: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // Captura genérica para otros errores (red, etc.)
            System.err.println("❌ Error creando materia: " + e.getMessage());
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }

    public List<Map<String, Object>> getSubjects() {
        try {
            // ASUME que tu tabla en Supabase se llama 'subjects' (en plural)
            String url = supabaseUrl + "/subjects?select=*"; // <-- CAMBIO DE TABLA
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            // Devuelve la lista de materias
            return Arrays.asList(response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo materias: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Map<String, Object>> getRequests() {
        try {
            // Selección explícita y orden descendente por id_request
            String url = supabaseUrl + "/requests?select=id_request,id_user,id_subject,grade,carreer_name,description,state,created_at&order=id_request.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            // Devuelve la lista de requests
            return Arrays.asList(response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener una request por id_request
    public Map<String, Object> getRequestById(String requestId) {
        try {
            String url = supabaseUrl + "/requests?id_request=eq." + requestId + "&select=id_request,id_user,id_subject,grade,carreer_name,description,state,created_at";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            if (rows != null && rows.length > 0) {
                return rows[0];
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo request por id_request: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> createRequest(Map<String, Object> requestData) {
        try {
            // ASUME que tu tabla en Supabase se llama 'requests'
            String url = supabaseUrl + "/requests?select=*";

            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=representation,resolution=merge-duplicates");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);

            System.out.println("🚀 Creando nueva request en Supabase...");

            // Pedimos un Map[] porque Supabase devuelve un array con el objeto creado
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map[].class);

            // Si Supabase devuelve un cuerpo y un array no vacío, la creación fue exitosa
            if (response.getBody() != null && response.getBody().length > 0) {
                System.out.println("✅ Request creada exitosamente.");
                // Devolvemos el primer objeto del array, que es nuestro grupo con su ID
                return response.getBody()[0];
            }

            // Si no, devolvemos un error
            System.err.println("❌ Supabase devolvió OK pero sin cuerpo de respuesta.");
            return Map.of("error", "Supabase devolvió OK pero sin cuerpo de respuesta.");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // ✅ ESTE ES EL CATCH MÁS IMPORTANTE
            // Captura errores 4xx y 5xx y muestra el error REAL de Supabase
            System.err.println("❌ Error REAL de Supabase al crear request: " + e.getResponseBodyAsString());
            return Map.of("error", "Error de Supabase: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // Captura genérica para otros errores (red, etc.)
            System.err.println("❌ Error creando request: " + e.getMessage());
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }
    public boolean deleteRequestById(String requestId) {
        try {
            // Filtrar por columna correcta: id_request
            String url = supabaseUrl + "/requests?id_request=eq." + requestId;

            HttpHeaders headers = createHeaders();
            // Para DELETE, 'minimal' es más eficiente que 'representation'
            headers.set("Prefer", "return=minimal");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("🚀 Eliminando request de Supabase (id_request): " + requestId);

            // Ejecutamos la llamada DELETE
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, String.class);

            // Si el código es 2xx (ej. 200 OK, 204 No Content), no lanzará excepción
            System.out.println("✅ Request eliminada exitosamente.");
            return true;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Captura errores 4xx (ej. 404 Not Found, 403 RLS policy)
            System.err.println("❌ Error REAL de Supabase al eliminar request: " + e.getResponseBodyAsString());
            return false;

        } catch (Exception e) {
            // Captura genérica para otros errores (red, etc.)
            System.err.println("❌ Error eliminando request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NUEVO: Obtener requests por ID de usuario
    public List<Map<String, Object>> getRequestsByUserId(String userId) {
        try {
            // Filtra en la base de datos con ?id_user=eq.USER_ID
            String url = supabaseUrl + "/requests?id_user=eq." + userId + "&select=id_request,id_user,id_subject,grade,carreer_name,description,state,created_at&order=id_request.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests por usuario: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener requests por ID de materia
    public List<Map<String, Object>> getRequestsBySubjectId(Long idSubject) {
        try {
            // Filtra en la base de datos con ?id_subject=eq.SUBJECT_ID
            String url = supabaseUrl + "/requests?id_subject=eq." + idSubject + "&select=id_request,id_user,id_subject,grade,carreer_name,description,state,created_at&order=id_request.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests por materia: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener requests por estado
    public List<Map<String, Object>> getRequestsByState(String state) {
        try {
            String url = supabaseUrl + "/requests?state=eq." + state + "&select=id_request,id_user,id_subject,grade,carreer_name,description,state,created_at&order=id_request.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo requests por estado: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ✅ NUEVO: Actualizar estado de una request por id
    public Map<String, Object> updateRequestState(String requestId, String state) {
        try {
            // Filtrar por columna correcta: id_request
            String url = supabaseUrl + "/requests?id_request=eq." + requestId;

            Map<String, Object> payload = new HashMap<>();
            payload.put("state", state);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.PATCH, entity, String.class);

        return Map.of(
            "status", "updated",
            "id_request", requestId,
            "state", state
        );
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ Error REAL de Supabase al actualizar estado: " + e.getResponseBodyAsString());
        return Map.of(
            "error", "Error de Supabase: " + e.getResponseBodyAsString(),
            "id_request", requestId
        );
        } catch (Exception e) {
            System.err.println("❌ Error actualizando estado de request: " + e.getMessage());
            e.printStackTrace();
        return Map.of(
            "error", e.getMessage(),
            "id_request", requestId
        );
        }
    }
    public List<Map<String, Object>> getTutors() {
        try {
            // ASUME que tu tabla en Supabase se llama 'tutors'
            String url = supabaseUrl + "/tutors?select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            // Devuelve la lista de tutores
            return Arrays.asList(response.getBody());

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo tutores: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener ID de tutor por id_user (si existe alguno)
    public Long getTutorIdByUser(String userId) {
        try {
            if (userId == null || userId.isBlank()) return null;

            String url = supabaseUrl + "/tutors?id_user=eq." + userId + "&select=id&limit=1";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            if (rows != null && rows.length > 0 && rows[0] != null) {
                Object idObj = rows[0].get("id");
                if (idObj != null) {
                    try {
                        return Long.parseLong(idObj.toString());
                    } catch (NumberFormatException ignore) {
                        return null;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo tutorId por usuario: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ✅ NUEVO: Obtener lista de id_subject donde el usuario es tutor (ACEPTADO)
    public List<Long> getTutorSubjectIdsByUser(String userId) {
        try {
            if (userId == null || userId.isBlank()) return new ArrayList<>();

            String url = supabaseUrl + "/tutors?id_user=eq." + userId + "&select=id_subject";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            List<Long> ids = new ArrayList<>();
            if (rows != null) {
                for (Map row : rows) {
                    Object val = row.get("id_subject");
                    if (val != null) {
                        try {
                            ids.add(Long.parseLong(val.toString()));
                        } catch (NumberFormatException ignore) { /* skip */ }
                    }
                }
            }
            return ids;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo id_subjects del tutor: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // ✅ NUEVO: Validar si un usuario ya es tutor de una materia específica
    public boolean isUserTutorOfSubject(String userId, Long idSubject) {
        try {
            if (userId == null || userId.isBlank() || idSubject == null) return false;

            String url = supabaseUrl + "/tutors?id_user=eq." + userId + "&id_subject=eq." + idSubject + "&select=id";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            return rows != null && rows.length > 0;
        } catch (Exception e) {
            System.err.println("❌ Error comprobando si usuario ya es tutor: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, no bloquear por defecto
            return false;
        }
    }

    // ✅ NUEVO: Verificar si ya existe una solicitud pendiente (EN_ESPERA) para user+subject
    public boolean hasPendingRequestForSubject(String userId, Long idSubject) {
        try {
            if (userId == null || userId.isBlank() || idSubject == null) return false;

            String url = supabaseUrl
                    + "/requests?id_user=eq." + userId
                    + "&id_subject=eq." + idSubject
                    + "&state=eq.EN_ESPERA&select=id_request&limit=1";

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            return rows != null && rows.length > 0;
        } catch (Exception e) {
            System.err.println("❌ Error comprobando solicitud pendiente existente: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, no bloquear por defecto
            return false;
        }
    }

    // ✅ NUEVO: Verificar si un usuario es tutor (tiene al menos un registro en la tabla tutors)
    public boolean isUserTutor(String userId) {
        try {
            if (userId == null || userId.isBlank()) return false;

            String url = supabaseUrl + "/tutors?id_user=eq." + userId + "&select=id&limit=1";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            boolean isTutor = rows != null && rows.length > 0;
            
            System.out.println("🔍 Usuario " + userId + " es tutor: " + isTutor);
            return isTutor;
        } catch (Exception e) {
            System.err.println("❌ Error verificando si usuario es tutor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> createTutor(Map<String, Object> tutorData) {
        try {
            // ASUME que tu tabla en Supabase se llama 'tutors'
            String url = supabaseUrl + "/tutors?select=*";

            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=representation,resolution=merge-duplicates");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(tutorData, headers);

            System.out.println("🚀 Creando nuevo tutor en Supabase...");

            // Pedimos un Map[] porque Supabase devuelve un array con el objeto creado
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map[].class);

            // Si Supabase devuelve un cuerpo y un array no vacío, la creación fue exitosa
            if (response.getBody() != null && response.getBody().length > 0) {
                System.out.println("✅ Tutor creado exitosamente.");
                // Devolvemos el primer objeto del array
                return response.getBody()[0];
            }

            // Si no, devolvemos un error
            System.err.println("❌ Supabase devolvió OK pero sin cuerpo de respuesta.");
            return Map.of("error", "Supabase devolvió OK pero sin cuerpo de respuesta.");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Captura errores 4xx y 5xx y muestra el error REAL de Supabase
            System.err.println("❌ Error REAL de Supabase al crear tutor: " + e.getResponseBodyAsString());
            return Map.of("error", "Error de Supabase: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // Captura genérica para otros errores (red, etc.)
            System.err.println("❌ Error creando tutor: " + e.getMessage());
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }

    // ===== GROUP_REQUEST METHODS =====

    // ✅ NUEVO: Obtener todas las solicitudes de grupo
    public List<Map<String, Object>> getGroupRequests() {
        try {
            // Ahora que la columna created_at existe, podemos ordenar por ella
            String url = supabaseUrl + "/group_requests?select=*&order=created_at.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo solicitudes de grupo: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener solicitudes de grupo por id_group
    public List<Map<String, Object>> getGroupRequestsByGroup(Long groupId) {
        try {
            // Ahora que la columna created_at existe, podemos ordenar por ella
            String url = supabaseUrl + "/group_requests?id_group=eq." + groupId + "&select=*&order=created_at.desc";
            System.out.println("🌐 [Supabase] Fetch group_requests by id_group -> URL: " + url);
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);
            Map[] body = response.getBody();
            List<Map<String, Object>> list = body != null ? Arrays.asList(body) : new ArrayList<>();
            System.out.println("📥 [Supabase] group_requests fetched: " + list.size());
            return list;
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo solicitudes por grupo: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Crear una solicitud de grupo
    public Map<String, Object> createGroupRequest(Map<String, Object> requestData) {
        try {
            String url = supabaseUrl + "/group_requests?select=*";

            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=representation,resolution=merge-duplicates");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestData, headers);

            System.out.println("🚀 Creando solicitud de grupo en Supabase...");

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map[].class);

            if (response.getBody() != null && response.getBody().length > 0) {
                System.out.println("✅ Solicitud de grupo creada exitosamente.");
                return response.getBody()[0];
            }

            System.err.println("❌ Supabase devolvió OK pero sin cuerpo de respuesta.");
            return Map.of("error", "Supabase devolvió OK pero sin cuerpo de respuesta.");

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ Error REAL de Supabase al crear solicitud de grupo: " + e.getResponseBodyAsString());
            return Map.of("error", "Error de Supabase: " + e.getResponseBodyAsString());

        } catch (Exception e) {
            System.err.println("❌ Error creando solicitud de grupo: " + e.getMessage());
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ NUEVO: Eliminar una solicitud de grupo por id
    public boolean deleteGroupRequestById(Long id) {
        try {
            String url = supabaseUrl + "/group_requests?id=eq." + id;

            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=minimal");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("🚀 Eliminando solicitud de grupo de Supabase (id): " + id);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.DELETE, entity, String.class);

            System.out.println("✅ Solicitud de grupo eliminada exitosamente.");
            return true;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ Error REAL de Supabase al eliminar solicitud de grupo: " + e.getResponseBodyAsString());
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error eliminando solicitud de grupo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NUEVO: Obtener solicitudes por id_tutor
    public List<Map<String, Object>> getGroupRequestsByTutor(Long tutorId) {
        try {
            String url = supabaseUrl + "/group_requests?id_tutor=eq." + tutorId + "&select=*&order=created_at.desc";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] body = response.getBody();
            return body != null ? Arrays.asList(body) : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo solicitudes por tutor: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Verificar existencia de solicitud por (id_tutor, id_group)
    public boolean existsGroupRequest(Long tutorId, Long groupId) {
        try {
            String url = supabaseUrl + "/group_requests?id_tutor=eq." + tutorId + "&id_group=eq." + groupId + "&select=id&limit=1";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            Map[] rows = response.getBody();
            return rows != null && rows.length > 0;
        } catch (Exception e) {
            System.err.println("❌ Error comprobando existencia de solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // Verificar si usuario es admin (nuevo campo bool is_admin en profiles)
    public boolean isUserAdmin(String userId) {
        try {
            if (userId == null || userId.isBlank()) return false;
            Map<String, Object> profile = getUserProfile(userId);
            if (profile == null) return false;
            Object flag = profile.get("is_admin");
            if (flag instanceof Boolean) return (Boolean) flag;
            if (flag instanceof String) {
                // Manejar casos donde Supabase devuelve "true"/"false"
                return Boolean.parseBoolean((String) flag);
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error verificando admin (profiles.is_admin): " + e.getMessage());
            return false;
        }
    }
}