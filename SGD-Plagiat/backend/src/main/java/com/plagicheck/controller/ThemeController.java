package com.plagicheck.controller;

import com.plagicheck.entity.Theme;
import com.plagicheck.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @PostMapping
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<Theme> createTheme(@RequestBody Theme theme) {
        return ResponseEntity.ok(themeService.createTheme(theme));
    }

    @GetMapping("/my-themes")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<List<Theme>> getMyThemes() {
        return ResponseEntity.ok(themeService.getMyThemes());
    }

    @GetMapping
    @PreAuthorize("hasRole('COORDINATEUR')")
    public ResponseEntity<List<Theme>> getAllThemes() {
        return ResponseEntity.ok(themeService.getAllThemes());
    }

    @GetMapping("/validated")
    @PreAuthorize("hasRole('DA')")
    public ResponseEntity<List<Theme>> getValidatedThemes() {
        return ResponseEntity.ok(themeService.getValidatedThemes());
    }

    @GetMapping("/validated-without-soutenance")
    @PreAuthorize("hasAnyRole('COORDINATEUR', 'DA')")
    public ResponseEntity<List<Theme>> getValidatedThemesWithoutSoutenance() {
        return ResponseEntity.ok(themeService.getValidatedThemesWithoutSoutenance());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theme> getThemeById(@PathVariable Long id) {
        return ResponseEntity.ok(themeService.getThemeById(id));
    }

    @PutMapping("/{id}/decide")
    @PreAuthorize("hasRole('COORDINATEUR')")
    public ResponseEntity<Theme> decideTheme(
            @PathVariable Long id,
            @RequestParam String decision,
            @RequestParam(required = false) String commentaire) {
        return ResponseEntity.ok(themeService.proposeDecision(id, decision, commentaire));
    }
}
