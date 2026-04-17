package com.plagicheck.controller;

import com.plagicheck.entity.Rapport;
import com.plagicheck.service.RapportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<Rapport> uploadRapport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("themeId") Long themeId) {
        return ResponseEntity.ok(rapportService.uploadRapport(file, themeId));
    }

    @GetMapping("/my-reports")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<Rapport>> getMyRapports() {
        return ResponseEntity.ok(rapportService.getMyRapports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rapport> getRapportById(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.getRapportById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINATEUR', 'DA')")
    public ResponseEntity<List<Rapport>> getAllRapports() {
        return ResponseEntity.ok(rapportService.getAllRapports());
    }
}
