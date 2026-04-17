package com.plagicheck.controller;

import com.plagicheck.dto.SoutenanceDTO;
import com.plagicheck.entity.Soutenance;
import com.plagicheck.service.SoutenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/soutenances")
@RequiredArgsConstructor
public class SoutenanceController {

    private final SoutenanceService soutenanceService;

    @GetMapping("/my-soutenance")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<Soutenance> getMySoutenance() {
        Soutenance soutenance = soutenanceService.getMySoutenance();
        if (soutenance == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(soutenance);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINATEUR', 'DA')")
    public ResponseEntity<List<Soutenance>> getAllSoutenances() {
        return ResponseEntity.ok(soutenanceService.getAllSoutenances());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDINATEUR', 'DA')")
    public ResponseEntity<Soutenance> createSoutenance(@RequestBody SoutenanceDTO dto) {
        return ResponseEntity.ok(soutenanceService.createSoutenance(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINATEUR', 'DA')")
    public ResponseEntity<Soutenance> updateSoutenance(@PathVariable Long id, @RequestBody SoutenanceDTO dto) {
        return ResponseEntity.ok(soutenanceService.updateSoutenance(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINATEUR', 'DA')")
    public ResponseEntity<Void> deleteSoutenance(@PathVariable Long id) {
        soutenanceService.deleteSoutenance(id);
        return ResponseEntity.noContent().build();
    }
}
