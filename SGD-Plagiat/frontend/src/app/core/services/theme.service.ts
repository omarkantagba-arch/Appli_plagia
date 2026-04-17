import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Theme } from '../models/theme.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private apiUrl = `${environment.apiUrl}/themes`;

  constructor(private http: HttpClient) {}

  createTheme(theme: any): Observable<Theme> {
    return this.http.post<Theme>(this.apiUrl, theme);
  }

  getMyThemes(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${this.apiUrl}/my-themes`);
  }

  getThemeById(id: number): Observable<Theme> {
    return this.http.get<Theme>(`${this.apiUrl}/${id}`);
  }

  getAllThemes(): Observable<Theme[]> {
    return this.http.get<Theme[]>(this.apiUrl);
  }

  getValidatedThemes(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${this.apiUrl}/validated`);
  }

  getValidatedThemesWithoutSoutenance(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${this.apiUrl}/validated-without-soutenance`);
  }

  decideTheme(id: number, decision: string, commentaire?: string): Observable<Theme> {
    const params: any = { decision };
    if (commentaire) {
      params.commentaire = commentaire;
    }
    return this.http.put<Theme>(`${this.apiUrl}/${id}/decide`, null, { params });
  }
}
