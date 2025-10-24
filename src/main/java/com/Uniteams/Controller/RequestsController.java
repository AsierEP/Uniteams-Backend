package com.Uniteams.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.RequestsService;

@RestController
@RequestMapping("/api/requests")
public class RequestsController {

    private final RequestsService requestsService;

    public RequestsController(RequestsService requestsService) {
        this.requestsService = requestsService;
    }

    // Listar por estado (ACEPTADO, DENEGADO, EN_ESPERA)
    @GetMapping("/state/{state}")
    public ResponseEntity<?> getByState(@PathVariable("state") String state) {
        try {
            if (state == null || state.isBlank()) {
                return ResponseEntity.badRequest().body("El estado es obligatorio");
            }
            String upper = state.trim().toUpperCase();
            if (!(upper.equals("ACEPTADO") || upper.equals("DENEGADO") || upper.equals("EN_ESPERA"))) {
                return ResponseEntity.badRequest().body("Estado inválido. Use ACEPTADO, DENEGADO o EN_ESPERA");
            }
            List<Map<String, Object>> list = requestsService.getRequestsByState(upper);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error obteniendo requests por estado: " + e.getMessage());
        }
    }

    // Eliminar una request por id_request
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable("id") Long id) {
        try {
            Map<String, Object> result = requestsService.deleteRequest(id);
            if (result.containsKey("error")) {
                return ResponseEntity.status(500).body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la request: " + e.getMessage());
        }
    }

    // Actualizar solo el estado de una request
    @PatchMapping("/{id}/state")
    public ResponseEntity<?> updateRequestState(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> body) {
        try {
            Object stateObj = body != null ? body.get("state") : null;
            if (stateObj == null) {
                return ResponseEntity.badRequest().body("El campo 'state' es obligatorio");
            }
            String state = stateObj.toString();
            Map<String, Object> result = requestsService.updateRequestState(id, state);
            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar el estado: " + e.getMessage());
        }
    }

    // Listar todas las requests (públicas)
    @GetMapping("/public")
    public ResponseEntity<List<Map<String, Object>>> listRequests() {
        try {
            List<Map<String, Object>> requests = requestsService.getRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Buscar requests por texto
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchRequests(@RequestParam String q) {
        try {
            List<Map<String, Object>> requests = requestsService.searchRequests(q);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Obtener requests del usuario actual (por token)
    @GetMapping("/my-requests")
    public ResponseEntity<List<Map<String, Object>>> getMyRequests(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            List<Map<String, Object>> mine = requestsService.getRequestsByUser(userId);
            return ResponseEntity.ok(mine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Crear nueva request (validando que id_user == sub del token)
    @PostMapping
    public ResponseEntity<?> createRequest(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Normalizar nombres de campos desde snake_case a camelCase (compatibilidad con el frontend)
            if (request.containsKey("id_user") && !request.containsKey("idUser")) {
                request.put("idUser", request.get("id_user"));
            }
            if (request.containsKey("id_subject") && !request.containsKey("idSubject")) {
                request.put("idSubject", request.get("id_subject"));
            }
            if (request.containsKey("carreer_name") && !request.containsKey("carreerName")) {
                request.put("carreerName", request.get("carreer_name"));
            }
            if (request.containsKey("created_at") && !request.containsKey("createdAt")) {
                request.put("createdAt", request.get("created_at"));
            }
            if (request.containsKey("description") && !request.containsKey("description")) {
                // no-op, ya coincide el nombre
            }
            if (request.containsKey("grade") && !request.containsKey("grade")) {
                // no-op, ya coincide el nombre
            }

            // Validar mínimos requeridos
            if (request.get("idUser") == null) {
                return ResponseEntity.badRequest().body("Falta el idUser (UUID del usuario)");
            }
            if (request.get("idSubject") == null) {
                return ResponseEntity.badRequest().body("Falta el idSubject");
            }

            String userIdFromToken = validateTokenAndGetUserId(authHeader);
            String userIdFromRequest = request.get("idUser").toString();
            if (!userIdFromToken.equals(userIdFromRequest)) {
                return ResponseEntity.badRequest().body("El ID de usuario no coincide con el token");
            }

            // Normalizar/validar estado: valores permitidos ACEPTADO, DENEGADO, EN_ESPERA
            Object stateObj = request.get("state");
            String state = (stateObj == null ? "EN_ESPERA" : stateObj.toString().trim());
            if (state.isEmpty()) state = "EN_ESPERA";
            String upper = state.toUpperCase();
            if (!(upper.equals("ACEPTADO") || upper.equals("DENEGADO") || upper.equals("EN_ESPERA"))) {
                return ResponseEntity.badRequest().body("Estado inválido. Use ACEPTADO, DENEGADO o EN_ESPERA");
            }
            request.put("state", upper);

            Map<String, Object> created = requestsService.createRequest(request);
            if (created != null && created.containsKey("error")) {
                String msg = String.valueOf(created.get("error"));
                // 409 Conflict para colisiones de negocio conocidas
                if (msg != null) {
                    String lower = msg.toLowerCase();
                    if (lower.contains("ya es tutor") || lower.contains("solicitud pendiente")) {
                        return ResponseEntity.status(409).body(created);
                    }
                }
                return ResponseEntity.badRequest().body(created);
            }
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear la request: " + e.getMessage());
        }
    }

    // Reutilizamos el extractor de userId por token del StudygroupController
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
}
