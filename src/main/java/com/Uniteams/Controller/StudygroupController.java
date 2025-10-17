package com.Uniteams.Controller;

import com.Uniteams.DTO.StudygroupsDTO;
import com.Uniteams.Entity.Studygroups;
import com.Uniteams.Security.SupabaseJwtUtil;
import com.Uniteams.Service.StudygroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/study-groups")
public class StudygroupController {

    @Autowired
    private StudygroupService studyGroupservice;

    @Autowired
    private SupabaseJwtUtil jwtUtil;

    // Crear nuevo grupo de estudio
    @PostMapping
    public ResponseEntity<?> createStudyGroup(
            @RequestBody StudygroupsDTO request,
            @RequestHeader("Authorization") String authHeader) {

        try {
            // Validar token y obtener usuario
            String userId = validateTokenAndGetUserId(authHeader);

            // Crear grupo
            Studygroups studyGroup = studyGroupservice.createStudyGroup(request, userId);

            return ResponseEntity.ok(studyGroup);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear el grupo: " + e.getMessage());
        }
    }

    // Obtener todos los grupos públicos
    @GetMapping("/public")
    public ResponseEntity<List<Studygroups>> getPublicStudyGroups() {
        List<Studygroups> groups = studyGroupservice.getPublicStudyGroups();
        return ResponseEntity.ok(groups);
    }

    // Buscar grupos públicos
    @GetMapping("/public/search")
    public ResponseEntity<List<Studygroups>> searchPublicGroups(@RequestParam String q) {
        List<Studygroups> groups = studyGroupservice.searchPublicGroups(q);
        return ResponseEntity.ok(groups);
    }

    // Obtener grupos por materia
    @GetMapping("/public/subject/{subject}")
    public ResponseEntity<List<Studygroups>> getGroupsBySubject(@PathVariable String subject) {
        List<Studygroups> groups = studyGroupservice.getStudyGroupsBySubject(subject);
        return ResponseEntity.ok(groups);
    }

    // Obtener grupos por tipo de sesión
    @GetMapping("/public/session-type/{sessionType}")
    public ResponseEntity<List<Studygroups>> getGroupsBySessionType(@PathVariable String sessionType) {
        try {
            Studygroups.SessionType type = Studygroups.SessionType.valueOf(sessionType);
            List<Studygroups> groups = studyGroupservice.getStudyGroupsBySessionType(type);
            return ResponseEntity.ok(groups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unirse a un grupo público
    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinStudyGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String userId = validateTokenAndGetUserId(authHeader);
            boolean success = studyGroupservice.joinStudyGroup(groupId, userId);

            if (success) {
                return ResponseEntity.ok("Te has unido al grupo exitosamente");
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al unirse al grupo");
        }
    }

    // Obtener grupos del usuario actual
    @GetMapping("/my-groups")
    public ResponseEntity<List<Studygroups>> getUserStudyGroups(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            List<Studygroups> groups = studyGroupservice.getUserStudyGroups(userId);
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