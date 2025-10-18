package com.Uniteams.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Uniteams.DTO.SubjectsDTO;
import com.Uniteams.Entity.Subjects;
import com.Uniteams.Repository.SubjectsRepo;

@Service
public class SubjectsService {

    @Autowired
    private SubjectsRepo subjectsRepo;

    // Crear una materia
    public Subjects createSubject(SubjectsDTO request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia es requerido");
        }

        // Verificar existencia por nombre
        Optional<Subjects> existing = subjectsRepo.findByName(request.getName().trim());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("La materia ya existe");
        }

        Subjects subject = new Subjects();
        subject.setName(request.getName().trim());
        // createdAt/updatedAt se inicializan en el constructor de la entidad

        return subjectsRepo.save(subject);
    }

    // Obtener todas las materias
    public List<Subjects> getAllSubjects() {
        return subjectsRepo.findAll();
    }

    // Buscar materias por término
    public List<Subjects> searchSubjects(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return subjectsRepo.findAll();
        }
        return subjectsRepo.searchByName(searchTerm.trim());
    }

    // Obtener por id
    public Optional<Subjects> getSubjectById(Long id) {
        return subjectsRepo.findById(id);
    }

    // Actualizar materia
    public Subjects updateSubject(Long id, SubjectsDTO request) {
        Optional<Subjects> opt = subjectsRepo.findById(id);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Materia no encontrada");
        }

        Subjects subject = opt.get();
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            subject.setName(request.getName().trim());
        }
        // Forzar updatedAt ahora
        subject.setUpdatedAt(LocalDateTime.now());

        return subjectsRepo.save(subject);
    }

    // Eliminar materia
    public boolean deleteSubject(Long id) {
        Optional<Subjects> opt = subjectsRepo.findById(id);
        if (opt.isPresent()) {
            subjectsRepo.delete(opt.get());
            return true;
        }
        return false;
    }
}
