import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RapportService } from '../../core/services/rapport.service';
import { ThemeService } from '../../core/services/theme.service';
import { AuthService } from '../../core/services/auth.service';
import { Rapport, Theme } from '../../core/models/theme.model';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  rapports: Rapport[] = [];
  themes: Theme[] = [];
  loading = false;
  showForm = false;
  currentUser = this.authService.getCurrentUser();
  selectedFile: File | null = null;
  selectedThemeId: number | null = null;
  uploadProgress = 0;
  errorMessage = '';

  constructor(
    private rapportService: RapportService,
    private themeService: ThemeService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    
    if (this.currentUser?.role === 'ETUDIANT') {
      this.themeService.getMyThemes().subscribe({
        next: (themes) => {
          this.themes = themes.filter(t => t.statut === 'VALIDE');
        },
        error: () => {}
      });

      this.rapportService.getMyRapports().subscribe({
        next: (rapports) => {
          this.rapports = rapports;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    } else {
      this.rapportService.getAllRapports().subscribe({
        next: (rapports: Rapport[]) => {
          this.rapports = rapports;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    }
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    this.errorMessage = '';
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const validTypes = ['application/pdf', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
      if (!validTypes.includes(file.type)) {
        this.errorMessage = 'Seuls les fichiers PDF et DOCX sont acceptés';
        this.selectedFile = null;
        return;
      }
      
      if (file.size > 50 * 1024 * 1024) {
        this.errorMessage = 'Le fichier ne doit pas dépasser 50 MB';
        this.selectedFile = null;
        return;
      }
      
      this.selectedFile = file;
      this.errorMessage = '';
    }
  }

  submitRapport(): void {
    if (!this.selectedFile || !this.selectedThemeId) {
      this.errorMessage = 'Veuillez sélectionner un thème et un fichier';
      return;
    }

    console.log('Submitting rapport:', {
      file: this.selectedFile.name,
      themeId: this.selectedThemeId
    });

    this.loading = true;
    this.errorMessage = '';

    const formData = new FormData();
    formData.append('file', this.selectedFile);
    formData.append('themeId', this.selectedThemeId.toString());

    this.rapportService.uploadRapport(formData).subscribe({
      next: (rapport) => {
        console.log('Rapport uploaded successfully:', rapport);
        this.rapports.unshift(rapport);
        this.selectedFile = null;
        this.selectedThemeId = null;
        this.showForm = false;
        this.loading = false;
        alert('Rapport déposé avec succès!');
      },
      error: (error) => {
        console.error('Error uploading rapport:', error);
        this.errorMessage = error.error?.message || 'Erreur lors du dépôt du rapport';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
