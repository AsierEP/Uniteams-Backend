package com.Uniteams.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class SupabaseApiService {

    private final String supabaseUrl = "https://zskuikxfcjobpygoueqp.supabase.co/rest/v1";
    private final String apiKey = "eyJhbGc101JIUzI1N1IsInRScCT6TkpXVCJ9,eyJpc3M101JzdXBhYnFzzSIsInJL71f5fnpza3";

    private final RestTemplate restTemplate;

    public SupabaseApiService() {
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=representation");
        return headers;
    }

    // Ejemplo: Obtener todos los grupos
    public List<Map<String, Object>> getStudyGroups() {
        try {
            String url = supabaseUrl + "/studygroups?select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());

            ResponseEntity<Map[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map[].class);

            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            System.err.println("Error obteniendo grupos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Ejemplo: Crear grupo
    public Map<String, Object> createStudyGroup(Map<String, Object> groupData) {
        try {
            String url = supabaseUrl + "/studygroups";
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(groupData, createHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error creando grupo: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }
}