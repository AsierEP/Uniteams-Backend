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

import com.Uniteams.Service.TutorsService;

@RestController
@RequestMapping("/api/tutors")
public class TutorsController {

    private final TutorsService tutorsService;

    public TutorsController(TutorsService tutorsService) {
        this.tutorsService = tutorsService;
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

            String userIdFromToken = validateTokenAndGetUserId(authHeader);
            String userIdFromRequest = request.get("idUser").toString();
            if (!userIdFromToken.equals(userIdFromRequest)) {
                return ResponseEntity.badRequest().body("El ID de usuario no coincide con el token");
            }

            Map<String, Object> created = tutorsService.createTutor(request);
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el tutor: " + e.getMessage());
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
}
