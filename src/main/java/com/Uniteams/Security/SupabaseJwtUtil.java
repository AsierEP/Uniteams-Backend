/*package com.Uniteams.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

@Component
public class SupabaseJwtUtil {

    @Value("${supabase.anonkey}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            System.out.println("✅ Token JWT válido");
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("❌ Token expirado: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.err.println("❌ Token mal formado: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error validando token: " + e.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            System.out.println("🔑 User ID del token: " + userId);
            return userId;

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo user ID: " + e.getMessage());
            throw new IllegalArgumentException("Token inválido");
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }
}*/