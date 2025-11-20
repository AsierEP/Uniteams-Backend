package com.Uniteams.Service;

// Deprecated: AdminsService ya no se usa porque la tabla admins fue reemplazada
// por el campo booleano profiles.is_admin. Mantener clase mínima para evitar
// errores de referencias antiguas hasta limpieza definitiva.
public class AdminsService {
    private final SupabaseApiService supabaseApiService;
    public AdminsService(SupabaseApiService supabaseApiService) { this.supabaseApiService = supabaseApiService; }
    public boolean isUserAdmin(String userId) { return supabaseApiService.isUserAdmin(userId); }
}
