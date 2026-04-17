import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { RapportService } from '../../core/services/rapport.service';
import { SoutenanceService } from '../../core/services/soutenance.service';
import { AuthResponse } from '../../core/models/user.model';
import { Theme, Rapport, Soutenance } from '../../core/models/theme.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  currentUser: AuthResponse | null = null;
  themes: Theme[] = [];
  rapports: Rapport[] = [];
  soutenance: Soutenance | null = null;
  loading = true;

  constructor(
    private authService: AuthService,
    private themeService: ThemeService,
    private rapportService: RapportService,
    private soutenanceService: SoutenanceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadData();
  }

  loadData(): void {
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

      this.rapportService.getMyRapports().subscribe({
        next: (rapports) => this.rapports = rapports,
        error: () => {}
      });

      this.soutenanceService.getMySoutenance().subscribe({
        next: (soutenance) => this.soutenance = soutenance,
        error: () => {}
      });
    } else {
      this.loading = false;
    }
  }

  countThemesByStatus(statut: string): number {
    return this.themes.filter(t => t.statut === statut).length;
  }

  hasValidTheme(): boolean {
    const hasValid = this.themes.length > 0 && this.themes.some(t => t.statut === 'VALIDE');
    console.log('hasValidTheme:', hasValid, 'themes:', this.themes);
    return hasValid;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateTo(route: string): void {
    console.log('Navigating to:', route);
    this.router.navigate([route]);
  }
}
