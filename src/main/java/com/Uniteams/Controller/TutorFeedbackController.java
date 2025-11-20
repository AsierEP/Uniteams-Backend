package com.Uniteams.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.TutorFeedbackService;
import com.Uniteams.Service.SupabaseApiService;

@RestController
@RequestMapping("/api/feedback/tutors")
public class TutorFeedbackController {

    private final TutorFeedbackService tutorFeedbackService;
    private final SupabaseApiService supabaseApiService;

    public TutorFeedbackController(TutorFeedbackService tutorFeedbackService, SupabaseApiService supabaseApiService) {
        this.tutorFeedbackService = tutorFeedbackService;
        this.supabaseApiService = supabaseApiService;
    }

    @PostMapping
    public ResponseEntity<?> createFeedback(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            String studentUserId = validateTokenAndGetUserId(authHeader);
            // Optionally verify tutor exists
            Object tutorIdObj = body.get("tutorId");
            if (tutorIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "tutorId requerido"));
            }
            Long tutorId;
            try { tutorId = tutorIdObj instanceof Number ? ((Number) tutorIdObj).longValue() : Long.parseLong(tutorIdObj.toString()); } catch (NumberFormatException e) { return ResponseEntity.badRequest().body(Map.of("error", "tutorId inválido")); }
            // Simple existencia: usar getTutorIdByUser? aquí asumimos tutorId válido si >0
            Map<String, Object> created = tutorFeedbackService.createFeedback(studentUserId, body);
            if (created.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(created);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tutorId}")
    public ResponseEntity<?> listFeedback(@PathVariable Long tutorId) {
        List<Map<String, Object>> feedback = tutorFeedbackService.getFeedbackByTutor(tutorId);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/{tutorId}/summary")
    public ResponseEntity<?> feedbackSummary(@PathVariable Long tutorId) {
        Map<String, Object> summary = tutorFeedbackService.getFeedbackSummary(tutorId);
        return ResponseEntity.ok(summary);
    }

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
