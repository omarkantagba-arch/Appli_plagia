package com.plagicheck.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SoutenanceDTO {
    private Long themeId;
    private LocalDate date;
    private LocalTime heure;
    private String salle;
    private String[] jury;
}
