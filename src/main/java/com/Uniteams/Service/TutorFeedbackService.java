package com.Uniteams.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TutorFeedbackService {

    private final SupabaseApiService supabaseApiService;

    public TutorFeedbackService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    public Map<String, Object> createFeedback(String studentUserId, Map<String, Object> body) {
        // Required fields: tutorId, stars; optional: studygroupId, comment
        Object tutorIdObj = body.get("tutorId");
        Object starsObj = body.get("stars");
        if (tutorIdObj == null || starsObj == null) {
            return Map.of("error", "tutorId y stars son requeridos");
        }
        Long tutorId;
        int stars;
        try {
            tutorId = tutorIdObj instanceof Number ? ((Number) tutorIdObj).longValue() : Long.parseLong(tutorIdObj.toString());
            stars = starsObj instanceof Number ? ((Number) starsObj).intValue() : Integer.parseInt(starsObj.toString());
        } catch (NumberFormatException e) {
            return Map.of("error", "Formato numérico inválido en tutorId o stars");
        }
        if (stars < 1 || stars > 5) {
            return Map.of("error", "stars debe estar entre 1 y 5");
        }
        Map<String, Object> supabaseData = new HashMap<>();
        supabaseData.put("tutor_id", tutorId);
        supabaseData.put("student_user_id", studentUserId);
        supabaseData.put("stars", stars);
        if (body.containsKey("studygroupId")) {
            Object sgObj = body.get("studygroupId");
            try {
                Long sg = sgObj instanceof Number ? ((Number) sgObj).longValue() : Long.parseLong(sgObj.toString());
                supabaseData.put("studygroup_id", sg);
            } catch (NumberFormatException ignore) { /* omit invalid studygroupId */ }
        }
        if (body.containsKey("comment")) {
            supabaseData.put("comment", body.get("comment"));
        }
        // timestamps (Supabase puede tener default now(); si no, enviamos explícito)
        supabaseData.put("created_at", OffsetDateTime.now().toString());
        supabaseData.put("updated_at", OffsetDateTime.now().toString());
        return supabaseApiService.createTutorFeedback(supabaseData);
    }

    public List<Map<String, Object>> getFeedbackByTutor(Long tutorId) {
        return supabaseApiService.getTutorFeedbackByTutorId(tutorId);
    }

    public Map<String, Object> getFeedbackSummary(Long tutorId) {
        return supabaseApiService.getTutorFeedbackSummary(tutorId);
    }
}
