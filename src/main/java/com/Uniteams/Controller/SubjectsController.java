package com.Uniteams.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.DTO.SubjectsDTO;
import com.Uniteams.Entity.Subjects;
import com.Uniteams.Security.SupabaseJwtUtil;
import com.Uniteams.Service.SubjectsService;

@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {

    @Autowired
    private SubjectsService subjectsService;

    @Autowired
    private SupabaseJwtUtil jwtUtil;

    // Crear nueva materia (requiere token)
    @PostMapping
    public ResponseEntity<?> createSubject(
            @RequestBody SubjectsDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            validateToken(authHeader);
            Subjects subject = subjectsService.createSubject(request);
            return ResponseEntity.ok(subject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear la materia: " + e.getMessage());
        }
    }

    // Obtener todas las materias
    @GetMapping("/public")
    public ResponseEntity<List<Subjects>> getAllSubjects() {
        List<Subjects> list = subjectsService.getAllSubjects();
        return ResponseEntity.ok(list);
    }

    // Buscar materias
    @GetMapping("/search")
    public ResponseEntity<List<Subjects>> searchSubjects(@RequestParam String q) {
        List<Subjects> list = subjectsService.searchSubjects(q);
        return ResponseEntity.ok(list);
    }

    // Obtener materia por id
    @GetMapping("/{id}")
    public ResponseEntity<Subjects> getSubjectById(@PathVariable Long id) {
        Optional<Subjects> opt = subjectsService.getSubjectById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Actualizar materia (requiere token)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectsDTO request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            validateToken(authHeader);
            Subjects updated = subjectsService.updateSubject(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la materia");
        }
    }

    // Eliminar materia (requiere token)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            validateToken(authHeader);
            boolean deleted = subjectsService.deleteSubject(id);
            if (deleted) return ResponseEntity.ok("Materia eliminada");
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar la materia");
        }
    }

    // Método auxiliar para validar token (similar al de StudygroupController)
    private void validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token de autorización inválido");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }
    }
}
