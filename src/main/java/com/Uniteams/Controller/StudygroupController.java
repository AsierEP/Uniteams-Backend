package com.Uniteams.Controller;

import com.Uniteams.Service.StudygroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-groups")
public class StudygroupController {

    private final StudygroupService studygroupService;

    public StudygroupController(StudygroupService studygroupService) {
        this.studygroupService = studygroupService;
    }

    // Obtener todos los grupos públicos
    @GetMapping("/public")
    public ResponseEntity<List<Map<String, Object>>> getPublicStudyGroups() {
        try {
            List<Map<String, Object>> groups = studygroupService.getPublicStudyGroups();
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Buscar grupos públicos
    @GetMapping("/public/search")
    public ResponseEntity<List<Map<String, Object>>> searchPublicGroups(@RequestParam String q) {
        try {
            List<Map<String, Object>> groups = studygroupService.searchPublicGroups(q);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener grupos por materia
    @GetMapping("/public/subject/{subject}")
    public ResponseEntity<List<Map<String, Object>>> getGroupsBySubject(@PathVariable String subject) {
        try {
            List<Map<String, Object>> groups = studygroupService.getGroupsBySubject(subject);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Crear nuevo grupo de estudio
    @PostMapping
    public ResponseEntity<?> createStudyGroup(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            System.out.println("📥 Datos recibidos del frontend:");
            System.out.println("Name: " + request.get("name"));
            System.out.println("Subject: " + request.get("subject"));
            System.out.println("Description: " + request.get("description"));
            System.out.println("SessionType: " + request.get("sessionType"));
            System.out.println("Code: " + request.get("code"));
            System.out.println("CreatedBy: " + request.get("createdBy"));
            System.out.println("MaxParticipants: " + request.get("maxParticipants"));
            System.out.println("IsPrivate: " + request.get("isPrivate"));

            // ✅ VERIFICAR QUE TENEMOS LOS DATOS CRÍTICOS
            if (request.get("createdBy") == null) {
                System.err.println("❌ ERROR: createdBy es null");
                return ResponseEntity.badRequest().body("Falta el ID del usuario creador");
            }

            if (request.get("code") == null) {
                System.err.println("❌ ERROR: code es null");
                return ResponseEntity.badRequest().body("Falta el código del grupo");
            }

            // ✅ VALIDAR TOKEN Y OBTENER USER ID
            String userIdFromToken = validateTokenAndGetUserId(authHeader);
            String userIdFromRequest = request.get("createdBy").toString();

            System.out.println("🔐 User ID from token: " + userIdFromToken);
            System.out.println("🔐 User ID from request: " + userIdFromRequest);

            // ✅ VERIFICAR QUE COINCIDEN LOS IDs
            if (!userIdFromToken.equals(userIdFromRequest)) {
                System.err.println("❌ ERROR: User ID mismatch");
                return ResponseEntity.badRequest().body("El ID de usuario no coincide con el token");
            }

            Map<String, Object> createdGroup = studygroupService.createStudyGroup(request);

            return ResponseEntity.ok(createdGroup);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error en backend: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al crear el grupo: " + e.getMessage());
        }
    }

    // ✅ CORREGIDO: Unirse a un grupo
    @PostMapping("/{code}/join")
    public ResponseEntity<?> joinStudyGroup(
            @PathVariable String code,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            Map<String, Object> result = studygroupService.joinStudyGroup(code, userId);

            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al unirse al grupo: " + e.getMessage()));
        }
    }

    // ✅ CORREGIDO: Salir de un grupo
    @PostMapping("/{code}/leave")
    public ResponseEntity<?> leaveStudyGroup(
            @PathVariable String code,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            Map<String, Object> result = studygroupService.leaveStudyGroup(code, userId);

            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al salir del grupo: " + e.getMessage()));
        }
    }

    // ✅ CORREGIDO: Obtener grupos del usuario actual (con detalles completos)
    @GetMapping("/my-groups")
    public ResponseEntity<?> getUserStudyGroups(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            List<Map<String, Object>> groups = studygroupService.getUserStudyGroupsWithDetails(userId);
            return ResponseEntity.ok(groups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos del usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error al cargar tus grupos"));
        }
    }

    // ✅ MÉTODO MEJORADO - Extracción más robusta del user ID
    private String validateTokenAndGetUserId(String authHeader) {
        System.out.println("🔐 Validando token...");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("❌ Header de autorización inválido");
            throw new IllegalArgumentException("Token de autorización inválido");
        }

        String token = authHeader.substring(7);
        System.out.println("📎 Token recibido (primeros 50 chars): " +
                (token.length() > 50 ? token.substring(0, 50) + "..." : token));

        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Token JWT malformado");
            }

            // Decodificar el payload
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            System.out.println("📋 Payload del token: " + payloadJson);

            // Parsear el JSON para extraer el sub (user ID)
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            String userId = (String) payload.get("sub");
            if (userId == null) {
                throw new IllegalArgumentException("No se encontró el campo 'sub' en el token");
            }

            System.out.println("✅ User ID extraído: " + userId);
            return userId;

        } catch (Exception e) {
            System.err.println("❌ Error extrayendo user ID: " + e.getMessage());
            throw new IllegalArgumentException("No se pudo extraer user ID del token: " + e.getMessage());
        }
    }

    // ✅ NUEVO: Obtener materias disponibles
    @GetMapping("/subjects")
    public ResponseEntity<List<String>> getAvailableSubjects() {
        try {
            List<String> subjects = studygroupService.getAvailableSubjects();
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ NUEVO: Obtener grupo por código
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getStudyGroupByCode(@PathVariable String code) {
        try {
            Map<String, Object> group = studygroupService.getStudyGroupByCode(code);
            if (group == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}