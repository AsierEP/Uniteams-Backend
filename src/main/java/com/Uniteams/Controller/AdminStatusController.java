package com.Uniteams.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.SupabaseApiService;

@RestController
@RequestMapping("/api/admin")
public class AdminStatusController {

    private final SupabaseApiService supabaseApiService;

    public AdminStatusController(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getAdminStatus(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = validateTokenAndGetUserId(authHeader);
            boolean isAdmin = supabaseApiService.isUserAdmin(userId);
            return ResponseEntity.ok(Map.of("user_id", userId, "is_admin", isAdmin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Nuevo endpoint para establecer el estado admin de otro usuario
    @PostMapping("/status")
    public ResponseEntity<?> setAdminStatus(@RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body) {
        try {
            String requesterId = validateTokenAndGetUserId(authHeader);
            if (!supabaseApiService.isUserAdmin(requesterId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Solo un admin puede cambiar estado admin"));
            }
            Object targetUserObj = body.get("userId");
            Object flagObj = body.get("isAdmin");
            if (targetUserObj == null || flagObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId e isAdmin requeridos"));
            }
            boolean flag;
            if (flagObj instanceof Boolean) {
                flag = (Boolean) flagObj;
            } else {
                flag = Boolean.parseBoolean(flagObj.toString());
            }
            String targetUserId = targetUserObj.toString();
            Map<String, Object> update = Map.of("is_admin", flag);
            boolean ok = supabaseApiService.updateUserProfile(targetUserId, update);
            if (ok) return ResponseEntity.ok(Map.of("success", true, "user_id", targetUserId, "is_admin", flag));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No se pudo actualizar perfil"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
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
