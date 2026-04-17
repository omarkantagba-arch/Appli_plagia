package com.plagicheck.service;

import com.plagicheck.entity.PlagiarismDetail;
import com.plagicheck.entity.Rapport;
import com.plagicheck.entity.Theme;
import com.plagicheck.entity.User;
import com.plagicheck.repository.PlagiarismDetailRepository;
import com.plagicheck.repository.RapportRepository;
import com.plagicheck.repository.ThemeRepository;
import com.plagicheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RapportService {

    private final RapportRepository rapportRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final FileStorageService fileStorageService;
    private final TextExtractionService textExtractionService;
    private final PlagiarismService plagiarismService;
    private final WebSearchService webSearchService;
    private final PlagiarismDetailRepository plagiarismDetailRepository;

    @Value("${plagiarism.threshold.auto-accept}")
    private int autoAcceptThreshold;

    @Value("${plagiarism.threshold.auto-reject}")
    private int autoRejectThreshold;

    public Rapport uploadRapport(MultipartFile file, Long themeId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User etudiant = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RuntimeException("Theme not found"));

        if (!theme.getEtudiant().getId().equals(etudiant.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // Store file
        String fileName = fileStorageService.storeFile(file);
        String fileUrl = fileStorageService.getFileUrl(fileName);

        // Extract text
        String textContent;
        try {
            textContent = textExtractionService.extractText(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from file", e);
        }

        log.info("Starting plagiarism detection for rapport on theme: {}", themeId);

        // PHASE 1: Analyse locale avec les rapports existants
        log.info("Phase 1: Local plagiarism detection");
        List<Rapport> existingRapports = rapportRepository.findAll();
        double localMaxSimilarity = 0.0;

        for (Rapport existing : existingRapports) {
            if (existing.getTextContent() != null) {
                double similarity = plagiarismService.calculateNGramSimilarity(
                        textContent, existing.getTextContent(), 3
                );
                localMaxSimilarity = Math.max(localMaxSimilarity, similarity);
            }
        }

        log.info("Local plagiarism score: {}%", localMaxSimilarity);

        // PHASE 2: Analyse web (toujours effectuée après l'analyse locale)
        log.info("Phase 2: Web plagiarism detection");
        double webMaxSimilarity = webSearchService.searchWebForPlagiarism(textContent);
        log.info("Web plagiarism score: {}%", webMaxSimilarity);

        // Score final = max(local, web)
        double finalScore = Math.max(localMaxSimilarity, webMaxSimilarity);
        log.info("Final plagiarism score: {}%", finalScore);

        // Determine version
        List<Rapport> themeRapports = rapportRepository.findByThemeId(themeId);
        int version = themeRapports.size() + 1;

        // Create rapport
        Rapport rapport = new Rapport();
        rapport.setFichierUrl(fileUrl);
        rapport.setTextContent(textContent);
        rapport.setPlagiatScore(finalScore);
        rapport.setVersion(version);
        rapport.setTheme(theme);
        rapport.setEtudiant(etudiant);

        // Auto-decision based on score
        if (finalScore < autoAcceptThreshold) {
            rapport.setStatut(Rapport.Statut.VALIDE);
            log.info("Rapport auto-validated (score < {}%)", autoAcceptThreshold);
        } else if (finalScore > autoRejectThreshold) {
            rapport.setStatut(Rapport.Statut.REJETE);
            log.info("Rapport auto-rejected (score > {}%)", autoRejectThreshold);
        } else {
            rapport.setStatut(Rapport.Statut.EN_EXAMEN);
            log.info("Rapport requires manual review ({}% <= score <= {}%)", autoAcceptThreshold, autoRejectThreshold);
        }

        // Save rapport
        Rapport savedRapport = rapportRepository.save(rapport);

        // Save plagiarism details
        PlagiarismDetail detail = new PlagiarismDetail();
        detail.setRapport(savedRapport);
        detail.setLocalScore(localMaxSimilarity);
        detail.setWebScore(webMaxSimilarity);
        detail.setFinalScore(finalScore);
        detail.setWebSearchPerformed(true);
        
        // Récupérer les sources web si le score web est significatif
        if (webMaxSimilarity > 10.0) {
            try {
                List<String> webSources = webSearchService.getWebSources(textContent);
                if (!webSources.isEmpty()) {
                    detail.setWebSources(String.join(", ", webSources));
                    log.info("Found {} web sources with high similarity", webSources.size());
                }
            } catch (Exception e) {
                log.warn("Could not retrieve web sources: {}", e.getMessage());
            }
        }
        
        plagiarismDetailRepository.save(detail);

        log.info("Rapport saved successfully with ID: {}", savedRapport.getId());

        return savedRapport;
    }

    public List<Rapport> getMyRapports() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return rapportRepository.findByEtudiantId(user.getId());
    }

    public Rapport getRapportById(Long id) {
        return rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport not found"));
    }

    public List<Rapport> getAllRapports() {
        return rapportRepository.findAll();
    }
}
