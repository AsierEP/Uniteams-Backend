package com.Uniteams.Controller;

import com.Uniteams.Security.SupabaseJwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SupabaseJwtUtil jwtUtil;

    public AuthController(SupabaseJwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/validate")
    public String validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Token inválido";
        }

        String token = authHeader.substring(7);
        if (jwtUtil.validateToken(token)) {
            String userId = jwtUtil.getUserIdFromToken(token);
            return "Token válido para usuario: " + userId;
        } else {
            return "Token inválido";
        }
    }
}