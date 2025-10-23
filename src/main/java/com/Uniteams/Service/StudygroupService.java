package com.Uniteams.Service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StudygroupService {

    private final SupabaseApiService supabaseApiService;

    public StudygroupService(SupabaseApiService supabaseApiService) {
        this.supabaseApiService = supabaseApiService;
    }

    // ✅ CORREGIDO: Mapear camelCase a snake_case para Supabase
    public Map<String, Object> createStudyGroup(Map<String, Object> groupData) {
        // Crear nuevo mapa con los nombres de columna correctos para Supabase
        Map<String, Object> supabaseData = new HashMap<>();

        // Mapeo seguro (HashMap.put acepta null sin problema)
        supabaseData.put("code", groupData.get("code"));
        supabaseData.put("name", groupData.get("name"));
        supabaseData.put("subject", groupData.get("subject"));
        supabaseData.put("meeting_day", groupData.get("meetingDay"));
        supabaseData.put("meeting_date", groupData.get("meetingDate"));
        supabaseData.put("meeting_time", groupData.get("meetingTime"));
        supabaseData.put("description", groupData.get("description"));
        supabaseData.put("max_participants", groupData.get("maxParticipants"));
        supabaseData.put("current_participants", groupData.get("currentParticipants"));
        supabaseData.put("is_private", groupData.get("isPrivate"));
        supabaseData.put("tutor_name", groupData.get("tutorName")); // Pasamos el null
        supabaseData.put("join_link", groupData.get("joinLink"));   // Pasamos el null
        supabaseData.put("created_by", groupData.get("createdBy"));

        // Manejo especial para 'sessionType' (el único que usa .toString())
        if (groupData.containsKey("sessionType")) {
            Object sessionTypeObj = groupData.get("sessionType");
            if (sessionTypeObj != null) {
                String sessionType = sessionTypeObj.toString();
                supabaseData.put("session_type", sessionType.toLowerCase());
                System.out.println("✅ Session Type mapeado: " + sessionType.toLowerCase());
            } else {
                supabaseData.put("session_type", null);
            }
        }

        System.out.println("📤 Datos mapeados para Supabase: " + supabaseData);

        Map<String, Object> createdGroup = supabaseApiService.createStudyGroup(supabaseData);

        // ✅✅ ESTA ES LA CORRECCIÓN MÁS IMPORTANTE ✅✅
        // Agregar el grupo al perfil del creador automáticamente
        if (createdGroup != null && !createdGroup.containsKey("error") && groupData.containsKey("createdBy")) {

            Object userIdObj = groupData.get("createdBy");
            Object groupIdObj = createdGroup.get("id"); // 1. Obtener el ID como Objeto

            // 2. Comprobar que NINGUNO sea null ANTES de usarlos
            if (userIdObj != null && groupIdObj != null) {
                String userId = userIdObj.toString();
                String groupId = groupIdObj.toString(); // 3. Ahora SÍ es seguro usar .toString()

                addGroupToUserProfile(userId, groupId);
                System.out.println("✅ Grupo agregado automáticamente al perfil del creador");

            } else if (groupIdObj == null) {
                // 4. Si el ID es nulo, es porque la inserción falló (Paso 1)
                System.err.println("❌ Error: El grupo se creó pero la BDD no devolvió un ID.");
                System.err.println("❌ Datos del grupo que falló: " + createdGroup);
            }

        } else if (createdGroup != null && createdGroup.containsKey("error")) {
            System.err.println("❌ Error de Supabase al crear grupo: " + createdGroup.get("error"));
        }

        return createdGroup;
    }

    // Obtener todos los grupos públicos
    public List<Map<String, Object>> getPublicStudyGroups() {
        try {
            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group -> !Boolean.TRUE.equals(group.get("is_private")))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos públicos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Buscar grupos públicos
    public List<Map<String, Object>> searchPublicGroups(String searchTerm) {
        try {
            List<Map<String, Object>> publicGroups = getPublicStudyGroups();

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return publicGroups;
            }

            String searchLower = searchTerm.toLowerCase();
            return publicGroups.stream()
                    .filter(group ->
                            (group.get("name") != null && group.get("name").toString().toLowerCase().contains(searchLower)) ||
                                    (group.get("description") != null && group.get("description").toString().toLowerCase().contains(searchLower)) ||
                                    (group.get("tutor_name") != null && group.get("tutor_name").toString().toLowerCase().contains(searchLower)) ||
                                    (group.get("subject") != null && group.get("subject").toString().toLowerCase().contains(searchLower))
                    )
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error buscando grupos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener grupos por materia
    public List<Map<String, Object>> getGroupsBySubject(String subject) {
        try {
            List<Map<String, Object>> publicGroups = getPublicStudyGroups();
            return publicGroups.stream()
                    .filter(group ->
                            subject != null &&
                                    group.get("subject") != null &&
                                    subject.equalsIgnoreCase(group.get("subject").toString()))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos por materia: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Obtener grupos del usuario (que ha creado)
    public List<Map<String, Object>> getUserStudyGroups(String userId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group ->
                            group.get("created_by") != null &&
                                    userId.equals(group.get("created_by").toString()))
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos del usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Obtener grupo por código
    public Map<String, Object> getStudyGroupByCode(String code) {
        try {
            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group ->
                            code != null &&
                                    group.get("code") != null &&
                                    code.equals(group.get("code").toString()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupo por código: " + e.getMessage());
            return null;
        }
    }

    // ✅ NUEVO: Actualizar participantes
    public Map<String, Object> updateParticipants(String code, int newParticipantCount) {
        try {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("current_participants", newParticipantCount);

            // Aquí necesitarías implementar un método update en SupabaseApiService
            // Por ahora, obtenemos el grupo y lo actualizamos manualmente
            Map<String, Object> group = getStudyGroupByCode(code);
            if (group != null) {
                group.put("current_participants", newParticipantCount);
                return group;
            }
            return Map.of("error", "Grupo no encontrado");
        } catch (Exception e) {
            System.err.println("❌ Error actualizando participantes: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ NUEVO: Obtener materias disponibles
    public List<String> getAvailableSubjects() {
        try {
            List<Map<String, Object>> publicGroups = getPublicStudyGroups();
            return publicGroups.stream()
                    .filter(group -> group.get("subject") != null)
                    .map(group -> group.get("subject").toString())
                    .distinct()
                    .sorted()
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo materias disponibles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ✅ CORREGIDO: Método para unirse a un grupo
    public Map<String, Object> joinStudyGroup(String groupCode, String userId) {
        try {
            // 1. Buscar el grupo por código
            Map<String, Object> group = getStudyGroupByCode(groupCode);
            if (group == null) {
                return Map.of("error", "Grupo no encontrado");
            }

            // ... (verificación de cupo está bien) ...
            Object currentParticipantsObj = group.get("current_participants");
            Object maxParticipantsObj = group.get("max_participants");
            if (currentParticipantsObj == null || maxParticipantsObj == null) {
                return Map.of("error", "Datos del grupo incompletos");
            }
            int currentParticipants = Integer.parseInt(currentParticipantsObj.toString());
            int maxParticipants = Integer.parseInt(maxParticipantsObj.toString());
            if (currentParticipants >= maxParticipants) {
                return Map.of("error", "El grupo está lleno");
            }

            // 3. Verificar si el usuario ya está en el grupo
            List<Long> userGroups = getUserStudyGroupIds(userId); // <-- AHORA ES List<Long>
            Object groupIdObj = group.get("id");

            if (groupIdObj == null) {
                return Map.of("error", "El grupo no tiene ID válido");
            }

            String groupId = groupIdObj.toString(); // ej: "11"
            Long groupIdAsLong = Long.parseLong(groupId); // ej: 11

            if (userGroups.contains(groupIdAsLong)) { // <-- Compara Long vs Long
                return Map.of("error", "Ya estás en este grupo");
            }

            // 4. Agregar el grupo al perfil del usuario
            // (El método addGroupToUserProfile espera un String, lo cual es correcto)
            boolean addedToProfile = addGroupToUserProfile(userId, groupId);
            if (!addedToProfile) {
                return Map.of("error", "Error al unirse al grupo");
            }

            // 5. Actualizar el contador de participantes
            int newParticipantCount = currentParticipants + 1;
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("current_participants", newParticipantCount);

            supabaseApiService.updateStudyGroup(groupId, updateData);

            System.out.println("✅ Usuario " + userId + " se unió al grupo " + groupCode);

            return Map.of(
                    "success", true,
                    "message", "Te has unido al grupo exitosamente",
                    "group", group
            );

        } catch (Exception e) {
            System.err.println("❌ Error uniéndose al grupo: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

// ✅ CORREGIDO: Obtener IDs de grupos del usuario (como Números)
    public List<Long> getUserStudyGroupIds(String userId) { // <-- CAMBIADO A List<Long>
        try {
            Map<String, Object> profile = supabaseApiService.getUserProfile(userId);
            if (profile != null && profile.containsKey("study_groups")) {
                Object studyGroups = profile.get("study_groups");

                if (studyGroups instanceof List) {
                    // Convertir la lista de Objects (que son Integers o Longs) a List<Long>
                    return ((List<?>) studyGroups).stream()
                            .filter(Objects::nonNull)
                            .map(obj -> Long.parseLong(obj.toString())) // Convertir a Long
                            .toList();
                }
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos del usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ✅ NUEVO: Agregar grupo al perfil del usuario
// ✅ CORREGIDO: Agregar grupo al perfil del usuario
    private boolean addGroupToUserProfile(String userId, String groupId) { // Sigue recibiendo String
        try {
            // Convertir el ID (String) a Long para guardarlo
            Long groupIdAsLong = Long.parseLong(groupId);

            // Obtener grupos actuales (ahora son Longs)
            List<Long> currentGroups = new ArrayList<>(getUserStudyGroupIds(userId));

            if (!currentGroups.contains(groupIdAsLong)) {
                currentGroups.add(groupIdAsLong); // Añadir el Long
            }

            // Actualizar el perfil
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("study_groups", currentGroups); // Enviar la List<Long> [11]

            return supabaseApiService.updateUserProfile(userId, updateData);

        } catch (Exception e) {
            System.err.println("❌ Error agregando grupo al perfil: " + e.getMessage());
            return false;
        }
    }

    // ✅ CORREGIDO: Obtener grupos completos del usuario (para el calendario)
    public List<Map<String, Object>> getUserStudyGroupsWithDetails(String userId) {
        try {
            List<Long> userGroupIds = getUserStudyGroupIds(userId); // <-- AHORA ES List<Long> [11]
            if (userGroupIds.isEmpty()) {
                return new ArrayList<>();
            }

            // Obtener todos los grupos y filtrar por los IDs del usuario
            List<Map<String, Object>> allGroups = supabaseApiService.getStudyGroups();
            return allGroups.stream()
                    .filter(group -> {
                        Object groupIdObj = group.get("id");
                        if (groupIdObj == null) return false;

                        // Convertir el ID del grupo a Long para comparar
                        try {
                            Long groupIdAsLong = Long.parseLong(groupIdObj.toString());
                            return userGroupIds.contains(groupIdAsLong); // <-- Compara Long vs Long
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .toList();

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo grupos detallados del usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    // ✅ CORREGIDO: Salir de un grupo
    public Map<String, Object> leaveStudyGroup(String groupCode, String userId) {
        try {
            // 1. Buscar el grupo por código
            Map<String, Object> group = getStudyGroupByCode(groupCode);
            if (group == null) {
                return Map.of("error", "Grupo no encontrado");
            }

            // 2. Verificar si el usuario está en el grupo
            List<Long> userGroups = getUserStudyGroupIds(userId); // <-- CAMBIO: Ahora es List<Long>
            Object groupIdObj = group.get("id");

            if (groupIdObj == null) {
                return Map.of("error", "El grupo no tiene ID válido");
            }

            String groupId = groupIdObj.toString(); // ej: "11"
            Long groupIdAsLong = Long.parseLong(groupId); // ej: 11

            if (!userGroups.contains(groupIdAsLong)) { // <-- CAMBIO: Compara Long vs Long
                return Map.of("error", "No estás en este grupo");
            }

            // 3. Remover el grupo del perfil del usuario
            // (El método removeGroupFromUserProfile espera un String, lo cual es correcto)
            boolean removedFromProfile = removeGroupFromUserProfile(userId, groupId);
            if (!removedFromProfile) {
                return Map.of("error", "Error al salir del grupo");
            }

            // 4. Actualizar el contador de participantes
            Object currentParticipantsObj = group.get("current_participants"); // <-- Corrección de NPE
            if (currentParticipantsObj != null) {
                int currentParticipants = Integer.parseInt(currentParticipantsObj.toString());
                int newParticipantCount = Math.max(0, currentParticipants - 1);
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("current_participants", newParticipantCount);

                supabaseApiService.updateStudyGroup(groupId, updateData); // <-- DESCOMENTA ESTO
            }

            System.out.println("✅ Usuario " + userId + " salió del grupo " + groupCode);

            return Map.of(
                    "success", true,
                    "message", "Has salido del grupo exitosamente"
            );

        } catch (Exception e) {
            System.err.println("❌ Error saliendo del grupo: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ CORREGIDO: Remover grupo del perfil del usuario
    private boolean removeGroupFromUserProfile(String userId, String groupId) { // Sigue recibiendo String
        try {
            Long groupIdAsLong = Long.parseLong(groupId);

            // Obtener grupos actuales (ahora son Longs)
            List<Long> currentGroups = new ArrayList<>(getUserStudyGroupIds(userId));

            // Remover el Long
            currentGroups.remove(groupIdAsLong);

            // Actualizar el perfil
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("study_groups", currentGroups); // Enviar la List<Long>

            return supabaseApiService.updateUserProfile(userId, updateData);

        } catch (Exception e) {
            System.err.println("❌ Error removiendo grupo del perfil: " + e.getMessage());
            return false;
        }
    }
}