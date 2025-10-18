package com.Uniteams.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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