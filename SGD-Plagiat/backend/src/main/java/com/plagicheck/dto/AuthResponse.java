package com.plagicheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String nom;
    private String prenom;
    private String role;
    private String filiere;
}
