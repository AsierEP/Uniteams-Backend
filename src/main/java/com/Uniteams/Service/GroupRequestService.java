package com.Uniteams.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Uniteams.DTO.GroupRequestDTO;

@Service
public class GroupRequestService {

    @Autowired
    private SupabaseApiService supabaseApiService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    // ✅ Crear una solicitud de grupo
    public GroupRequestDTO createGroupRequest(GroupRequestDTO groupRequestDTO) {
        // Mapear camelCase a snake_case para Supabase
        Map<String, Object> data = new HashMap<>();
        data.put("id_group", groupRequestDTO.getIdGroup());
        data.put("id_tutor", groupRequestDTO.getIdTutor());
        
        // Agregar tutor_name si está presente
        if (groupRequestDTO.getTutorName() != null) {
            data.put("tutor_name", groupRequestDTO.getTutorName());
        }

        System.out.println("🚀 Creando solicitud de grupo: " + data);

        Map<String, Object> response = supabaseApiService.createGroupRequest(data);

        if (response.containsKey("error")) {
            throw new RuntimeException("Error al crear solicitud de grupo: " + response.get("error"));
        }

        // Mapear respuesta de Supabase a DTO
        return mapToDTO(response);
    }

    // ✅ Obtener todas las solicitudes de grupo
    public List<GroupRequestDTO> getAllGroupRequests() {
        List<Map<String, Object>> requests = supabaseApiService.getGroupRequests();
        return requests.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Obtener solicitudes de grupo por id_group
    public List<GroupRequestDTO> getRequestsByGroup(Long groupId) {
        List<Map<String, Object>> requests = supabaseApiService.getGroupRequestsByGroup(groupId);
        return requests.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NUEVO: Obtener solicitudes por tutor
    public List<GroupRequestDTO> getRequestsByTutor(Long tutorId) {
        List<Map<String, Object>> requests = supabaseApiService.getGroupRequestsByTutor(tutorId);
        return requests.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // ✅ NUEVO: Existe solicitud para (id_tutor, id_group)
    public boolean existsRequestForTutorAndGroup(Long tutorId, Long groupId) {
        if (tutorId == null || groupId == null) return false;
        return supabaseApiService.existsGroupRequest(tutorId, groupId);
    }

    // ✅ Eliminar una solicitud de grupo
    public boolean deleteGroupRequest(Long id) {
        System.out.println("🚀 Eliminando solicitud de grupo con id: " + id);
        return supabaseApiService.deleteGroupRequestById(id);
    }

    // ✅ Mapear Map a DTO (snake_case → camelCase)
    private GroupRequestDTO mapToDTO(Map<String, Object> data) {
        GroupRequestDTO dto = new GroupRequestDTO();
        
        if (data.get("id") != null) {
            dto.setId(((Number) data.get("id")).longValue());
        }
        
        if (data.get("id_group") != null) {
            dto.setIdGroup(((Number) data.get("id_group")).longValue());
        }
        
        if (data.get("id_tutor") != null) {
            dto.setIdTutor(((Number) data.get("id_tutor")).longValue());
        }
        
        if (data.get("tutor_name") != null) {
            dto.setTutorName(data.get("tutor_name").toString());
        }
        
        if (data.get("created_at") != null) {
            dto.setCreatedAt(data.get("created_at").toString());
        }
        
        return dto;
    }
}
