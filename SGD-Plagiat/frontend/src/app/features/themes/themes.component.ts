import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ThemeService } from '../../core/services/theme.service';
import { AuthService } from '../../core/services/auth.service';
import { Theme } from '../../core/models/theme.model';

@Component({
  selector: 'app-themes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './themes.component.html',
  styleUrls: ['./themes.component.css']
})
export class ThemesComponent implements OnInit {
  themes: Theme[] = [];
  loading = false;
  showForm = false;
  currentUser = this.authService.getCurrentUser();
  
  newTheme = {
    titre: '',
    description: '',
    filiere: this.currentUser?.filiere || ''
  };

  selectedTheme: Theme | null = null;
  showDecisionModal = false;
  decision = '';
  commentaire = '';

  constructor(
    private themeService: ThemeService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadThemes();
  }

  loadThemes(): void {
    this.loading = true;
    if (this.currentUser?.role === 'ETUDIANT') {
      this.themeService.getMyThemes().subscribe({
        next: (themes) => {
          this.themes = themes;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    } else if (this.currentUser?.role === 'COORDINATEUR') {
      this.themeService.getAllThemes().subscribe({
        next: (themes) => {
          this.themes = themes;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
    } else if (this.currentUser?.role === 'DA') {
      this.themeService.getValidatedThemes().subscribe({
        next: (themes) => {
          this.themes = themes;
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
  }

  submitTheme(): void {
    if (!this.newTheme.titre || !this.newTheme.description) {
      return;
    }

    this.loading = true;
    this.themeService.createTheme(this.newTheme).subscribe({
      next: (theme) => {
        this.themes.unshift(theme);
        this.newTheme = {
          titre: '',
          description: '',
          filiere: this.currentUser?.filiere || ''
        };
        this.showForm = false;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  openDecisionModal(theme: Theme): void {
    this.selectedTheme = theme;
    this.showDecisionModal = true;
    this.decision = '';
    this.commentaire = '';
  }

  closeDecisionModal(): void {
    this.showDecisionModal = false;
    this.selectedTheme = null;
    this.decision = '';
    this.commentaire = '';
  }

  submitDecision(): void {
    if (!this.selectedTheme || !this.decision) {
      return;
    }

    this.loading = true;
    this.themeService.decideTheme(this.selectedTheme.id, this.decision, this.commentaire).subscribe({
      next: (updatedTheme) => {
        const index = this.themes.findIndex(t => t.id === updatedTheme.id);
        if (index !== -1) {
          this.themes[index] = updatedTheme;
        }
        this.closeDecisionModal();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateToReports(): void {
    this.router.navigate(['/reports']);
  }

  navigateToSoutenances(): void {
    this.router.navigate(['/soutenances']);
  }
}
