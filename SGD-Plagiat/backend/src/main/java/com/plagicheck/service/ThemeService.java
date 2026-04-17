package com.plagicheck.service;

import com.plagicheck.entity.Theme;
import com.plagicheck.entity.User;
import com.plagicheck.repository.ThemeRepository;
import com.plagicheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final PlagiarismService plagiarismService;

    public Theme createTheme(Theme theme) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User etudiant = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        theme.setEtudiant(etudiant);
        theme.setStatut(Theme.Statut.EN_ATTENTE);

        // Calculer le score de similarité
        List<Theme> existingThemes = themeRepository.findByFiliere(theme.getFiliere());
        double maxSimilarity = 0.0;
        
        for (Theme existing : existingThemes) {
            double similarity = plagiarismService.calculateSimilarity(
                theme.getTitre() + " " + theme.getDescription(),
                existing.getTitre() + " " + existing.getDescription()
            );
            maxSimilarity = Math.max(maxSimilarity, similarity);
        }

        theme.setPlagiatScore(maxSimilarity);

        return themeRepository.save(theme);
    }

    public List<Theme> getMyThemes() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return themeRepository.findByEtudiantId(user.getId());
    }

    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    public Theme getThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));
    }

    public Theme proposeDecision(Long id, String decision, String commentaire) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User coordinateur = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        theme.setCoordinateur(coordinateur);
        theme.setCommentaire(commentaire);

        if ("VALIDER".equals(decision)) {
            theme.setStatut(Theme.Statut.VALIDE);
        } else if ("REJETER".equals(decision)) {
            theme.setStatut(Theme.Statut.REJETE);
        }

        return themeRepository.save(theme);
    }

    public List<Theme> getThemesByFiliere(String filiere) {
        return themeRepository.findByFiliere(filiere);
    }

    public List<Theme> getValidatedThemes() {
        return themeRepository.findByStatut(Theme.Statut.VALIDE);
    }

    public List<Theme> getValidatedThemesWithoutSoutenance() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Theme> themes = themeRepository.findValidatedThemesWithoutSoutenance();
        
        if (user.getRole() == User.Role.COORDINATEUR) {
            return themes.stream()
                    .filter(t -> t.getFiliere().equals(user.getFiliere()))
                    .toList();
        }
        return themes;
    }
}
