package com.Uniteams.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.SupabaseApiService;
import com.Uniteams.Service.TutorsService;

@RestController
@RequestMapping("/api/tutors")
public class TutorsController {

    private final TutorsService tutorsService;
    private final SupabaseApiService supabaseApiService;

    public TutorsController(TutorsService tutorsService, SupabaseApiService supabaseApiService) {
        this.tutorsService = tutorsService;
        this.supabaseApiService = supabaseApiService;
    }

    // Listar todos los tutors (públicos)
    @GetMapping("/public")
    public ResponseEntity<List<Map<String, Object>>> listTutors() {
        try {
            List<Map<String, Object>> tutors = tutorsService.getTutors();
            return ResponseEntity.ok(tutors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener tutors por materia
    @GetMapping("/subject/{id}")
    public ResponseEntity<List<Map<String, Object>>> getTutorsBySubject(@PathVariable("id") Long idSubject) {
        try {
            List<Map<String, Object>> tutors = tutorsService.getTutorsBySubject(idSubject);
            return ResponseEntity.ok(tutors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener tutors del usuario actual (por token)
    @GetMapping("/my-tutors")
    public ResponseEntity<List<Map<String, Object>>> getMyTutors(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            List<Map<String, Object>> mine = tutorsService.getTutorsByUser(userId);
            return ResponseEntity.ok(mine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Crear nuevo tutor (validando que id_user == sub del token)
    @PostMapping
    public ResponseEntity<?> createTutor(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (request.get("idUser") == null) {
                return ResponseEntity.badRequest().body("Falta el idUser (UUID del usuario)");
            }
            if (request.get("idSubject") == null) {
                return ResponseEntity.badRequest().body("Falta el idSubject");
            }
            // VALIDACIÓN DESACTIVADA TEMPORALMENTE
            // En una fase posterior se validará que solo ADMIN pueda crear tutores
            // y se verificará que el id del token tenga permiso para esta acción.

            Map<String, Object> created = tutorsService.createTutor(request);
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el tutor: " + e.getMessage());
        }
    }

    
    // ✅ NUEVO: Crear tutor desde una request (solo ADMIN)
    @PostMapping("/from-request/{id_request}")
    public ResponseEntity<?> createTutorFromRequest(
            @PathVariable("id_request") Long idRequest,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (!isAdmin(authHeader)) {
                return ResponseEntity.status(403).body("Acceso denegado: se requiere rol ADMIN");
            }

            Map<String, Object> result = tutorsService.createTutorFromRequest(idRequest);

            if (result.containsKey("error")) {
                Object msg = result.get("error");
                if ("Request no encontrada".equals(msg)) {
                    return ResponseEntity.status(404).body(result);
                }
                return ResponseEntity.badRequest().body(result);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear tutor desde request: " + e.getMessage());
        }
    }

    // Extracción de userId del token (igual patrón que StudygroupController)
    private String validateTokenAndGetUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorización inválido");
        }
        String token = authHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Token JWT malformado");
            }
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);
            String userId = (String) payload.get("sub");
            if (userId == null) {
                throw new IllegalArgumentException("No se encontró el campo 'sub' en el token");
            }
            return userId;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo extraer user ID del token: " + e.getMessage());
        }
    }

    // ✅ Chequeo de rol ADMIN desde token y/o perfil en Supabase
    private boolean isAdmin(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return false;
            }
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            // 1) Revisar claims habituales
            Object roleClaim = payload.get("role");
            if (roleClaim != null && "ADMIN".equalsIgnoreCase(roleClaim.toString())) {
                return true;
            }
            // app_metadata.roles: ["ADMIN", ...]
            Object appMeta = payload.get("app_metadata");
            if (appMeta instanceof Map) {
                Object roles = ((Map<?, ?>) appMeta).get("roles");
                if (roles instanceof List) {
                    for (Object r : (List<?>) roles) {
                        if ("ADMIN".equalsIgnoreCase(String.valueOf(r))) return true;
                    }
                }
                Object singleRole = ((Map<?, ?>) appMeta).get("role");
                if (singleRole != null && "ADMIN".equalsIgnoreCase(singleRole.toString())) {
                    return true;
                }
            }
            // user_metadata.role
            Object userMeta = payload.get("user_metadata");
            if (userMeta instanceof Map) {
                Object r = ((Map<?, ?>) userMeta).get("role");
                if (r != null && "ADMIN".equalsIgnoreCase(r.toString())) return true;
            }

            // 2) Si no está en el token, consultar perfiles en Supabase
            String userId = (String) payload.get("sub");
            if (userId != null) {
                Map<String, Object> profile = supabaseApiService.getUserProfile(userId);
                if (profile != null) {
                    Object pr = profile.get("role");
                    if (pr != null && "ADMIN".equalsIgnoreCase(pr.toString())) return true;
                    Object pr2 = profile.get("rol"); // por si el campo está en español
                    if (pr2 != null && "ADMIN".equalsIgnoreCase(pr2.toString())) return true;
                }
            }
        } catch (Exception ignore) {
            return false;
        }
        return false;
    }
}
