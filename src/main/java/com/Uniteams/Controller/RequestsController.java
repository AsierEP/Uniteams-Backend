package com.Uniteams.Controller;

import com.Uniteams.Service.RequestsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestsController {

    private final RequestsService requestsService;

    public RequestsController(RequestsService requestsService) {
        this.requestsService = requestsService;
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
            if (request.get("idUser") == null) {
                return ResponseEntity.badRequest().body("Falta el idUser (UUID del usuario)");
            }
            if (request.get("idSubject") == null) {
                return ResponseEntity.badRequest().body("Falta el idSubject");
            }
            if (request.get("grade") == null) {
                return ResponseEntity.badRequest().body("Falta el grade");
            }
            if (request.get("carreerName") == null) {
                return ResponseEntity.badRequest().body("Falta el carreerName");
            }

            String userIdFromToken = validateTokenAndGetUserId(authHeader);
            String userIdFromRequest = request.get("idUser").toString();
            if (!userIdFromToken.equals(userIdFromRequest)) {
                return ResponseEntity.badRequest().body("El ID de usuario no coincide con el token");
            }

            Map<String, Object> created = requestsService.createRequest(request);
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
