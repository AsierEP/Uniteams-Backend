package com.Uniteams.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.StudygroupService;
import com.Uniteams.Service.SupabaseApiService;

@RestController
@RequestMapping("/api/groups")
public class GroupsController {

    private final StudygroupService studygroupService;
    private final SupabaseApiService supabaseApiService;

    public GroupsController(StudygroupService studygroupService, SupabaseApiService supabaseApiService) {
        this.studygroupService = studygroupService;
        this.supabaseApiService = supabaseApiService;
    }

    // ✅ Usuario autenticado deja el grupo
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long groupId
    ) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);

            Map<String, Object> profile = supabaseApiService.getUserProfile(userId);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Perfil no encontrado"));
            }

            Object studyGroupsObj = profile.get("study_groups");
            java.util.List<Object> studyGroups = new java.util.ArrayList<>();
            if (studyGroupsObj instanceof java.util.List) {
                studyGroups.addAll((java.util.List<?>) studyGroupsObj);
            }

            String gidStr = groupId.toString();
            boolean contained = studyGroups.removeIf(g -> gidStr.equals(String.valueOf(g)));
            if (!contained) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El usuario no pertenece a ese grupo"));
            }

            Map<String, Object> update = new HashMap<>();
            update.put("study_groups", studyGroups);

            boolean ok = supabaseApiService.updateUserProfile(userId, update);
            if (ok) return ResponseEntity.ok(Map.of("success", true));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No se pudo actualizar el perfil"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ DELETE para eliminar miembro (id_user + id_group) — solo el propio usuario o el coordinador del grupo
    

    // ✅ Obtener grupo por ID
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable Long groupId) {
        Map<String, Object> group = studygroupService.getStudyGroupByIdNum(groupId);
        return (group != null && !group.isEmpty())
                ? ResponseEntity.ok(group)
                : ResponseEntity.notFound().build();
    }

    // ✅ Asignar tutor a un grupo por ID (solo coordinador)
    @PutMapping("/{groupId}/assign-tutor")
    public ResponseEntity<?> assignTutor(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long groupId,
            @RequestBody Map<String, Object> body
    ) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);

            // Verificar que el usuario sea el coordinador del grupo
            Map<String, Object> group = supabaseApiService.getStudyGroupByIdNum(groupId);
            if (group == null || group.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Grupo no encontrado"));
            }
            String createdBy = (String) group.get("created_by");
            if (createdBy == null || !createdBy.equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No autorizado: solo el coordinador puede asignar tutores"));
            }

            // Leer tutorId y tutorName del body (admite tutorId/tutor_id y tutorName/tutor_name)
            Object tutorIdObj = body.getOrDefault("tutorId", body.get("tutor_id"));
            if (tutorIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "tutorId requerido"));
            }

            Long tutorId;
            try {
                if (tutorIdObj instanceof Number) {
                    tutorId = ((Number) tutorIdObj).longValue();
                } else {
                    tutorId = Long.parseLong(tutorIdObj.toString());
                }
            } catch (NumberFormatException nfe) {
                return ResponseEntity.badRequest().body(Map.of("error", "tutorId inválido"));
            }

            String tutorName = null;
            Object tn = body.getOrDefault("tutorName", body.get("tutor_name"));
            if (tn != null) tutorName = tn.toString();

            Map<String, Object> update = new HashMap<>();
            update.put("tutor_id", tutorId);
            if (tutorName != null && !tutorName.isBlank()) {
                update.put("tutor_name", tutorName);
            }

            boolean ok = studygroupService.updateStudyGroupById(groupId, update);
            return ok
                    ? ResponseEntity.ok(Map.of("success", true))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "No se pudo actualizar el grupo"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // Helper para extraer userId del token
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
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido: " + e.getMessage());
        }
    }
}
