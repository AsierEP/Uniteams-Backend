package com.Uniteams.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.SubjectsService;

@RestController
@RequestMapping("/api/subjects")
public class SubjectsController {

	private final SubjectsService subjectsService;

	public SubjectsController(SubjectsService subjectsService) {
		this.subjectsService = subjectsService;
	}

	// Listar todas las materias
	@GetMapping("/public")
	public ResponseEntity<List<Map<String, Object>>> listSubjects() {
		try {
			List<Map<String, Object>> subjects = subjectsService.getSubjects();
			return ResponseEntity.ok(subjects);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	// Buscar materias por nombre
	@GetMapping("/search")
	public ResponseEntity<List<Map<String, Object>>> searchSubjects(@RequestParam String q) {
		try {
			List<Map<String, Object>> subjects = subjectsService.searchSubjects(q);
			return ResponseEntity.ok(subjects);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	// Obtener materia por id
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> getSubjectById(@PathVariable("id") Long idSubject) {
		try {
			Map<String, Object> subject = subjectsService.getSubjectById(idSubject);
			if (subject == null) return ResponseEntity.notFound().build();
			return ResponseEntity.ok(subject);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	// Crear nueva materia
	@PostMapping
	public ResponseEntity<?> createSubject(@RequestBody Map<String, Object> request) {
		try {
			if (request.get("name") == null || request.get("name").toString().isBlank()) {
				return ResponseEntity.badRequest().body("El nombre de la materia es obligatorio");
			}
			Map<String, Object> created = subjectsService.createSubject(request);
			return ResponseEntity.ok(created);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error al crear la materia: " + e.getMessage());
		}
	}
}

