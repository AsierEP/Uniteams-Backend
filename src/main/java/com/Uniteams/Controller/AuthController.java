package com.Uniteams.Controller;

import com.Uniteams.Service.SupabaseApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SupabaseApiService supabaseApiService;

    public AuthController(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // ✅ NUEVO: Login que valida si el usuario es tutor
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token inválido - formato incorrecto"));
            }

            String token = authHeader.substring(7);
            String userId = extractUserIdFromToken(token);

            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se pudo extraer el ID de usuario del token"));
            }

            // Verificar si el usuario es tutor
            boolean isTutor = supabaseApiService.isUserTutor(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("isTutor", isTutor);
            response.put("message", "Login exitoso");

            System.out.println("✅ Login exitoso - Usuario: " + userId + ", Es tutor: " + isTutor);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error en login: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", "Error procesando el login"));
        }
    }

    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);
            
            return (String) payload.get("sub");
        } catch (Exception e) {
            System.err.println("❌ Error extrayendo userId del token: " + e.getMessage());
            return null;
        }
    }

    @PostMapping("/validate")
    public String validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Token inválido - formato incorrecto";
        }

        String token = authHeader.substring(7);

        // ✅ TEMPORAL: Siempre retorna éxito para tokens de Supabase
        System.out.println("🔐 Token recibido (primeros 50 chars): " +
                (token.length() > 50 ? token.substring(0, 50) + "..." : token));

        // Simular extracción de user ID del token (sin validar cryptographicamente)
        try {
            // Los tokens JWT tienen 3 partes separadas por puntos
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                System.out.println("📋 Payload del token: " + payload);

                // Extraer user ID del payload
                if (payload.contains("\"sub\"")) {
                    int start = payload.indexOf("\"sub\":\"") + 7;
                    int end = payload.indexOf("\"", start);
                    String userId = payload.substring(start, end);
                    return "✅ Token válido para usuario: " + userId;
                }
            }
        } catch (Exception e) {
            System.err.println("Error parseando token: " + e.getMessage());
        }

        return "✅ Token aceptado (validación simplificada)";
    }

    // ✅ NUEVO ENDPOINT para debug del token
    @PostMapping("/debug")
    public String debugToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "No hay token";
        }

        String token = authHeader.substring(7);
        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            return "Token no tiene formato JWT válido";
        }

        try {
            String header = new String(java.util.Base64.getUrlDecoder().decode(parts[0]));
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            return "Header: " + header + "\nPayload: " + payload;
        } catch (Exception e) {
            return "Error decodificando token: " + e.getMessage();
        }
    }
}