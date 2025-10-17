package com.Uniteams.Service;

import com.Uniteams.DTO.StudygroupsDTO;
import com.Uniteams.Entity.Studygroups;
import com.Uniteams.Repository.StudygroupsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudygroupService {

    @Autowired
    private StudygroupsRepo Studygroupsrepo;

    public Studygroups createStudyGroup(StudygroupsDTO request, String userId) {
        // Validaciones
        if (request.getMaxParticipants() < 8) {
            throw new IllegalArgumentException("El número mínimo de participantes es 8");
        }

        if (request.getMeetingDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser en el pasado");
        }

        Studygroups studyGroup = new Studygroups();
        studyGroup.setName(request.getName());
        studyGroup.setSubject(request.getSubject());
        studyGroup.setSessionType(request.getSessionType());
        studyGroup.setMeetingDate(request.getMeetingDate());
        studyGroup.setMeetingTime(request.getMeetingTime());
        studyGroup.setDescription(request.getDescription());
        studyGroup.setMaxParticipants(request.getMaxParticipants());
        studyGroup.setIsPrivate(request.getIsPrivate() != null ? request.getIsPrivate() : false);
        studyGroup.setTutorName(request.getTutorName());
        studyGroup.setJoinLink(request.getJoinLink());
        studyGroup.setCreatedBy(userId);
        studyGroup.setCurrentParticipants(1); // El creador es el primer participante

        return Studygroupsrepo.save(studyGroup);
    }

    public List<Studygroups> getPublicStudyGroups() {
        return Studygroupsrepo.findByIsPrivateFalse();
    }

    public List<Studygroups> searchPublicGroups(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Studygroupsrepo.findByIsPrivateFalse();
        }
        return Studygroupsrepo.searchPublicGroups(searchTerm.trim());
    }

    public List<Studygroups> getStudyGroupsBySubject(String subject) {
        return Studygroupsrepo.findBySubjectAndIsPrivateFalse(subject);
    }

    public List<Studygroups> getStudyGroupsBySessionType(Studygroups.SessionType sessionType) {
        return Studygroupsrepo.findBySessionTypeAndIsPrivateFalse(sessionType);
    }

    public Optional<Studygroups> getStudyGroupByCode(String code) {
        return Studygroupsrepo.findByCode(code);
    }

    public List<Studygroups> getUserStudyGroups(String userId) {
        return Studygroupsrepo.findByCreatedBy(userId);
    }

    public boolean joinStudyGroup(Long groupId, String userId) {
        Optional<Studygroups> groupOpt = Studygroupsrepo.findById(groupId);
        if (groupOpt.isPresent()) {
            Studygroups group = groupOpt.get();

            // Verificar si hay cupo disponible
            if (group.getCurrentParticipants() >= group.getMaxParticipants()) {
                throw new IllegalStateException("El grupo está lleno");
            }

            // Verificar si es privado (aquí podrías agregar lógica de invitaciones)
            if (group.getIsPrivate()) {
                throw new IllegalStateException("Este grupo es privado, necesitas una invitación");
            }

            // Incrementar participantes (en una app real, guardarías en una tabla de participantes)
            group.setCurrentParticipants(group.getCurrentParticipants() + 1);
            Studygroupsrepo.save(group);
            return true;
        }
        return false;
    }
}