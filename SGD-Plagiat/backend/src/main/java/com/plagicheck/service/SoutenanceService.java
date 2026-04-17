package com.plagicheck.service;

import com.plagicheck.dto.SoutenanceDTO;
import com.plagicheck.entity.Soutenance;
import com.plagicheck.entity.Theme;
import com.plagicheck.entity.User;
import com.plagicheck.repository.SoutenanceRepository;
import com.plagicheck.repository.ThemeRepository;
import com.plagicheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoutenanceService {

    private final SoutenanceRepository soutenanceRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final NotificationService notificationService;

    public Soutenance getMySoutenance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return soutenanceRepository.findByEtudiantId(user.getId())
                .stream()
                .findFirst()
                .orElse(null);
    }

    public List<Soutenance> getAllSoutenances() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == User.Role.COORDINATEUR) {
            return soutenanceRepository.findAll().stream()
                    .filter(s -> s.getTheme().getFiliere().equals(user.getFiliere()))
                    .toList();
        }
        return soutenanceRepository.findAll();
    }

    @Transactional
    public Soutenance createSoutenance(SoutenanceDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User coordinateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Theme theme = themeRepository.findById(dto.getThemeId())
                .orElseThrow(() -> new RuntimeException("Theme not found"));
        
        if (theme.getStatut() != Theme.Statut.VALIDE) {
            throw new RuntimeException("Theme must be validated before scheduling soutenance");
        }
        
        if (coordinateur.getRole() == User.Role.COORDINATEUR && 
            !theme.getFiliere().equals(coordinateur.getFiliere())) {
            throw new RuntimeException("Coordinateur can only schedule soutenances for their filiere");
        }
        
        if (soutenanceRepository.findByThemeId(theme.getId()).isPresent()) {
            throw new RuntimeException("Soutenance already scheduled for this theme");
        }
        
        Soutenance soutenance = new Soutenance();
        soutenance.setTheme(theme);
        soutenance.setEtudiant(theme.getEtudiant());
        soutenance.setDate(dto.getDate());
        soutenance.setHeure(dto.getHeure());
        soutenance.setSalle(dto.getSalle());
        soutenance.setJury(dto.getJury());
        
        Soutenance saved = soutenanceRepository.save(soutenance);
        log.info("Soutenance scheduled for theme {} by {}", theme.getId(), coordinateur.getEmail());
        
        notificationService.createNotification(
            theme.getEtudiant(),
            "Soutenance programmée",
            String.format("Votre soutenance est programmée le %s à %s en salle %s", 
                dto.getDate(), dto.getHeure(), dto.getSalle())
        );
        
        return saved;
    }

    @Transactional
    public Soutenance updateSoutenance(Long id, SoutenanceDTO dto) {
        Soutenance soutenance = soutenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soutenance not found"));
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == User.Role.COORDINATEUR && 
            !soutenance.getTheme().getFiliere().equals(user.getFiliere())) {
            throw new RuntimeException("Coordinateur can only update soutenances for their filiere");
        }
        
        soutenance.setDate(dto.getDate());
        soutenance.setHeure(dto.getHeure());
        soutenance.setSalle(dto.getSalle());
        soutenance.setJury(dto.getJury());
        
        Soutenance updated = soutenanceRepository.save(soutenance);
        log.info("Soutenance {} updated by {}", id, user.getEmail());
        
        notificationService.createNotification(
            soutenance.getEtudiant(),
            "Soutenance modifiée",
            String.format("Votre soutenance a été modifiée: %s à %s en salle %s", 
                dto.getDate(), dto.getHeure(), dto.getSalle())
        );
        
        return updated;
    }

    @Transactional
    public void deleteSoutenance(Long id) {
        Soutenance soutenance = soutenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soutenance not found"));
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == User.Role.COORDINATEUR && 
            !soutenance.getTheme().getFiliere().equals(user.getFiliere())) {
            throw new RuntimeException("Coordinateur can only delete soutenances for their filiere");
        }
        
        notificationService.createNotification(
            soutenance.getEtudiant(),
            "Soutenance annulée",
            "Votre soutenance a été annulée. Vous serez informé de la nouvelle date."
        );
        
        soutenanceRepository.delete(soutenance);
        log.info("Soutenance {} deleted by {}", id, user.getEmail());
    }
}
