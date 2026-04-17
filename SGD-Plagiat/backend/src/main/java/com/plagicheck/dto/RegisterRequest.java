package com.plagicheck.dto;

import com.plagicheck.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank
    private String role;

    private String filiere;
}
