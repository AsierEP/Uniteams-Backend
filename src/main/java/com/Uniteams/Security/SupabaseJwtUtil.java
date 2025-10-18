package com.Uniteams.Security;

import org.springframework.stereotype.Component;

@Component
public class SupabaseJwtUtil {

    // ✅ TEMPORAL: Sin @Value, sin validación compleja
    public boolean validateToken(String token) {
        System.out.println("🔐 Validando token: " + token);
        // Por ahora siempre devuelve true para pruebas
        return token != null && !token.isEmpty();
    }

    public String getUserIdFromToken(String token) {
        return "user-test-id";
    }

    public String getEmailFromToken(String token) {
        return "test@uniteams.com";
    }
}