package com.Uniteams.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class SubjectsService {

	private final SupabaseApiService supabaseApiService;

	public SubjectsService(SupabaseApiService supabaseApiService) {
		this.supabaseApiService = supabaseApiService;
	}

	// Mapear camelCase -> snake_case y crear subject en Supabase
	public Map<String, Object> createSubject(Map<String, Object> subjectData) {
		Map<String, Object> supabaseData = new HashMap<>();

		if (subjectData.containsKey("idSubject")) {
			supabaseData.put("id_subject", subjectData.get("idSubject"));
		}
		if (subjectData.containsKey("name")) {
			supabaseData.put("name", subjectData.get("name"));
		}
		if (subjectData.containsKey("createdAt")) {
			supabaseData.put("created_at", subjectData.get("createdAt"));
		}
		if (subjectData.containsKey("updatedAt")) {
			supabaseData.put("updated_at", subjectData.get("updatedAt"));
		}

		System.out.println("📤 Subject mapeado para Supabase: " + supabaseData);
		return supabaseApiService.createSubject(supabaseData);
	}

	// Listar todos los subjects
	public List<Map<String, Object>> getSubjects() {
		try {
			return supabaseApiService.getSubjects();
		} catch (Exception e) {
			System.err.println("❌ Error obteniendo subjects: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// Buscar subjects por nombre (contains, case-insensitive)
	public List<Map<String, Object>> searchSubjects(String searchTerm) {
		try {
			List<Map<String, Object>> all = getSubjects();
			if (searchTerm == null || searchTerm.trim().isEmpty()) {
				return all;
			}
			String s = searchTerm.toLowerCase();
			return all.stream()
					.filter(row -> row.get("name") != null && row.get("name").toString().toLowerCase().contains(s))
					.toList();
		} catch (Exception e) {
			System.err.println("❌ Error buscando subjects: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// Obtener subject por id (filtrando en memoria)
	public Map<String, Object> getSubjectById(Long idSubject) {
		try {
			if (idSubject == null) return null;
			List<Map<String, Object>> all = supabaseApiService.getSubjects();
			return all.stream()
					.filter(s -> {
						Object id = s.get("id_subject");
						if (id == null) return false;
						try {
							return Long.parseLong(id.toString()) == idSubject;
						} catch (NumberFormatException nfe) {
							return false;
						}
					})
					.findFirst()
					.orElse(null);
		} catch (Exception e) {
			System.err.println("❌ Error obteniendo subject por id: " + e.getMessage());
			return null;
		}
	}
}

