package com.Uniteams.Controller;

import com.Uniteams.DTO.GroupRequestDTO;
import com.Uniteams.Service.GroupRequestService;
import com.Uniteams.Service.SupabaseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group-requests")
public class GroupRequestController {

    @Autowired
    private GroupRequestService groupRequestService;

    @Autowired
    private SupabaseApiService supabaseApiService;

    // ✅ Crear una solicitud de grupo (tutor aplica a un grupo)
    @PostMapping
    public ResponseEntity<?> createGroupRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody GroupRequestDTO groupRequestDTO) {
        
        try {
            System.out.println("🔵 Iniciando creación de solicitud de grupo...");
            System.out.println("📦 DTO recibido: idGroup=" + groupRequestDTO.getIdGroup() + ", idTutor=" + groupRequestDTO.getIdTutor());
            
            // Validar token y obtener userId
            String userId = validateTokenAndGetUserId(authHeader);
            System.out.println("✅ UserId extraído: " + userId);

            // Obtener o crear el tutor_id del usuario actual
            Long tutorId = supabaseApiService.getTutorIdByUser(userId);
            System.out.println("🔍 TutorId obtenido: " + tutorId);
            
            if (tutorId == null) {
                // Si no es tutor, crear registro básico en la tabla tutors
                System.out.println("⚠️ Usuario no es tutor aún. Creando registro básico...");
                
                Map<String, Object> tutorData = Map.of("id_user", userId);
                Map<String, Object> result = supabaseApiService.createTutor(tutorData);
                
                if (result.containsKey("error")) {
                    System.err.println("❌ Error al crear tutor: " + result.get("error"));
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message", "Error al crear registro de tutor: " + result.get("error")));
                }
                
                // Obtener el id del tutor recién creado
                tutorId = ((Number) result.get("id")).longValue();
                System.out.println("✅ Registro de tutor creado con id: " + tutorId);
            }

            // Establecer el id_tutor en el DTO
            groupRequestDTO.setIdTutor(tutorId);
            
            // Obtener el nombre del tutor desde el perfil del usuario
            Map<String, Object> profile = supabaseApiService.getUserProfile(userId);
            if (profile != null && profile.get("full_name") != null) {
                String tutorName = profile.get("full_name").toString();
                groupRequestDTO.setTutorName(tutorName);
                System.out.println("👤 Nombre del tutor: " + tutorName);
            }
            
            System.out.println("📝 Datos finales - idGroup: " + groupRequestDTO.getIdGroup() + ", idTutor: " + groupRequestDTO.getIdTutor() + ", tutorName: " + groupRequestDTO.getTutorName());

            // Evitar duplicados: comprobar si ya existe solicitud para (tutor, grupo)
            if (groupRequestDTO.getIdGroup() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "idGroup es requerido"));
            }
            boolean exists = groupRequestService.existsRequestForTutorAndGroup(tutorId, groupRequestDTO.getIdGroup());
            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Ya has enviado una solicitud para este grupo"));
            }

            // Crear la solicitud
            GroupRequestDTO created = groupRequestService.createGroupRequest(groupRequestDTO);
            System.out.println("✅ Solicitud creada exitosamente: " + created.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de autorización: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace(); // Log del stack trace para debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al crear solicitud: " + e.getMessage()));
        }
    }

    // ✅ NUEVO: Listar mis solicitudes (del tutor autenticado) devolviendo solo idGroup
    @GetMapping("/tutor/my-requests")
    public ResponseEntity<?> getMyRequests(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);

            // Obtener tutorId del usuario
            Long tutorId = supabaseApiService.getTutorIdByUser(userId);
            if (tutorId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No eres tutor"));
            }

        List<GroupRequestDTO> requests = groupRequestService.getRequestsByTutor(tutorId);
        List<Map<String, Object>> result = requests.stream()
            .map(req -> {
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("idGroup", req.getIdGroup());
            return m;
            })
            .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Obtener solicitudes de un grupo específico (solo coordinador del grupo)
    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getRequestsByGroup(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long groupId) {
        
        try {
            // Validar token y obtener userId
            System.out.println("🔎 [GroupRequests] GET group requests -> groupId=" + groupId);
            String userId = validateTokenAndGetUserId(authHeader);
            System.out.println("👤 [GroupRequests] Coordinator candidate userId=" + userId);

            // Verificar que el usuario sea el coordinador (created_by) del grupo
            Map<String, Object> group = supabaseApiService.getStudyGroupByIdNum(groupId);
            System.out.println("📦 [GroupRequests] Loaded group by id=" + groupId + ": " + group);
            if (group == null || group.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Grupo no encontrado"));
            }

            String createdBy = (String) group.get("created_by");
            System.out.println("🔐 [GroupRequests] created_by=" + createdBy);
            if (!userId.equals(createdBy)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Solo el coordinador del grupo puede ver las solicitudes"));
            }

            // Obtener solicitudes del grupo
            List<GroupRequestDTO> requests = groupRequestService.getRequestsByGroup(groupId);
            System.out.println("✅ [GroupRequests] Requests found: " + (requests != null ? requests.size() : 0));

            return ResponseEntity.ok(requests);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al obtener solicitudes: " + e.getMessage()));
        }
    }

    // ✅ Eliminar una solicitud de grupo (coordinador o admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        try {
            // Validar token y obtener userId
            String userId = validateTokenAndGetUserId(authHeader);

            // TODO: Aquí podrías verificar que el usuario sea el coordinador del grupo
            // o el tutor que creó la solicitud, pero por simplicidad eliminamos directamente

            boolean deleted = groupRequestService.deleteGroupRequest(id);

            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Solicitud eliminada exitosamente"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Error al eliminar solicitud"));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al eliminar solicitud: " + e.getMessage()));
        }
    }

    // ✅ Helper method para validar token y extraer userId
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

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error validando token: " + e.getMessage());
            throw new IllegalArgumentException("Token inválido: " + e.getMessage());
        }
    }
}
