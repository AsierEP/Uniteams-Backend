package com.Uniteams.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Uniteams.Service.SupabaseApiService;

@RestController
@RequestMapping("/api")
public class GroupMembersController {

    private final SupabaseApiService supabaseApiService;

    public GroupMembersController(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // DELETE /api/group_members?id_user=eq.{userId}&id_group=eq.{groupId}
    @DeleteMapping("/group_members")
    public ResponseEntity<?> deleteGroupMember(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "id_user") String idUser,
            @RequestParam(name = "id_group") String idGroup
    ) {
        try {
            String requesterId = validateTokenAndGetUserId(authHeader);

            // Permitir si es el mismo usuario
            boolean allowed = requesterId.equals(idUser);

            // Si no es el mismo, verificar si requester es coordinador del grupo
            if (!allowed) {
                Long gid = null;
                try { gid = Long.parseLong(idGroup); } catch (Exception ignore) {}
                Map<String, Object> group = gid != null ? supabaseApiService.getStudyGroupByIdNum(gid) : supabaseApiService.getStudyGroupById(idGroup);
                if (group != null && group.containsKey("created_by") && requesterId.equals(String.valueOf(group.get("created_by")))) {
                    allowed = true;
                }
            }

            if (!allowed) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No autorizado"));
            }

            Map<String, Object> profile = supabaseApiService.getUserProfile(idUser);
            if (profile == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Perfil no encontrado"));

            Object studyGroupsObj = profile.get("study_groups");
            java.util.List<Object> studyGroups = new java.util.ArrayList<>();
            if (studyGroupsObj instanceof java.util.List) studyGroups.addAll((java.util.List<?>) studyGroupsObj);

            boolean removed = studyGroups.removeIf(g -> idGroup.equals(String.valueOf(g)));
            if (!removed) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El usuario no pertenece a ese grupo"));

            Map<String, Object> update = new HashMap<>();
            update.put("study_groups", studyGroups);

            boolean ok = supabaseApiService.updateUserProfile(idUser, update);
            if (ok) return ResponseEntity.ok(Map.of("success", true));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "No se pudo actualizar el perfil"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // Copia del helper para validar token y extraer sub
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
