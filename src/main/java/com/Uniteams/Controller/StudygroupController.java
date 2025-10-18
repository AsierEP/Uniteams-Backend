package com.Uniteams.Controller;

import com.Uniteams.Security.SupabaseJwtUtil;
import com.Uniteams.Service.StudygroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-groups")
public class StudygroupController {

    private final StudygroupService studygroupService;
    private final SupabaseJwtUtil jwtUtil;

    public StudygroupController(StudygroupService studygroupService, SupabaseJwtUtil jwtUtil) {
        this.studygroupService = studygroupService;
        this.jwtUtil = jwtUtil;
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
            String userId = validateTokenAndGetUserId(authHeader);
            Map<String, Object> createdGroup = studygroupService.createStudyGroup(request, userId);
            return ResponseEntity.ok(createdGroup);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el grupo: " + e.getMessage());
        }
    }

    // Obtener grupos del usuario actual
    @GetMapping("/my-groups")
    public ResponseEntity<List<Map<String, Object>>> getUserStudyGroups(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            List<Map<String, Object>> groups = studygroupService.getUserStudyGroups(userId);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Método auxiliar para validar token
    private String validateTokenAndGetUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorización inválido");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        return jwtUtil.getUserIdFromToken(token);
    }
}