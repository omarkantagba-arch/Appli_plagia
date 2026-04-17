import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SoutenanceService } from '../../core/services/soutenance.service';
import { ThemeService } from '../../core/services/theme.service';
import { AuthService } from '../../core/services/auth.service';
import { Soutenance } from '../../core/models/theme.model';
import { SoutenanceDTO } from '../../core/models/soutenance.model';
import { Theme } from '../../core/models/theme.model';

@Component({
  selector: 'app-soutenances',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './soutenances.component.html',
  styleUrls: ['./soutenances.component.css']
})
export class SoutenancesComponent implements OnInit {
  soutenances: Soutenance[] = [];
  validatedThemes: Theme[] = [];
  showModal = false;
  isEditMode = false;
  currentSoutenanceId?: number;
  
  soutenanceForm: SoutenanceDTO = {
    themeId: 0,
    date: '',
    heure: '',
    salle: '',
    jury: []
  };
  
  juryInput = '';

  constructor(
    private soutenanceService: SoutenanceService,
    private themeService: ThemeService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadSoutenances();
    this.loadValidatedThemes();
  }

  loadSoutenances(): void {
    this.soutenanceService.getAllSoutenances().subscribe({
      next: (data) => this.soutenances = data,
      error: (err) => console.error('Error loading soutenances:', err)
    });
  }

  loadValidatedThemes(): void {
    this.themeService.getValidatedThemesWithoutSoutenance().subscribe({
      next: (data) => {
        this.validatedThemes = data;
      },
      error: (err) => console.error('Error loading themes:', err)
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.resetForm();
    this.showModal = true;
  }

  openEditModal(soutenance: Soutenance): void {
    this.isEditMode = true;
    this.currentSoutenanceId = soutenance.id;
    this.soutenanceForm = {
      themeId: soutenance.theme.id,
      date: soutenance.date,
      heure: soutenance.heure,
      salle: soutenance.salle,
      jury: [...soutenance.jury]
    };
    this.juryInput = soutenance.jury.join(', ');
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.resetForm();
  }

  resetForm(): void {
    this.soutenanceForm = {
      themeId: 0,
      date: '',
      heure: '',
      salle: '',
      jury: []
    };
    this.juryInput = '';
    this.currentSoutenanceId = undefined;
  }

  addJuryMember(): void {
    if (this.juryInput.trim()) {
      this.soutenanceForm.jury = this.juryInput.split(',').map(j => j.trim()).filter(j => j);
    }
  }

  submitForm(): void {
    this.addJuryMember();
    
    if (!this.soutenanceForm.themeId || !this.soutenanceForm.date || 
        !this.soutenanceForm.heure || !this.soutenanceForm.salle || 
        this.soutenanceForm.jury.length === 0) {
      alert('Veuillez remplir tous les champs');
      return;
    }

    if (this.isEditMode && this.currentSoutenanceId) {
      this.soutenanceService.updateSoutenance(this.currentSoutenanceId, this.soutenanceForm).subscribe({
        next: () => {
          this.loadSoutenances();
          this.closeModal();
        },
        error: (err) => alert('Erreur: ' + (err.error?.message || 'Impossible de modifier la soutenance'))
      });
    } else {
      this.soutenanceService.createSoutenance(this.soutenanceForm).subscribe({
        next: () => {
          this.loadSoutenances();
          this.loadValidatedThemes();
          this.closeModal();
        },
        error: (err) => alert('Erreur: ' + (err.error?.message || 'Impossible de créer la soutenance'))
      });
    }
  }

  deleteSoutenance(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir annuler cette soutenance ?')) {
      this.soutenanceService.deleteSoutenance(id).subscribe({
        next: () => {
          this.loadSoutenances();
          this.loadValidatedThemes();
        },
        error: (err) => alert('Erreur: ' + (err.error?.message || 'Impossible de supprimer la soutenance'))
      });
    }
  }

  getThemeTitle(themeId: number): string {
    const theme = this.validatedThemes.find(t => t.id === themeId);
    return theme ? theme.titre : '';
  }
}
