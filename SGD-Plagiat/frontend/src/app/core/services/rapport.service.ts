import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rapport } from '../models/theme.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RapportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  uploadRapport(formData: FormData): Observable<Rapport> {
    return this.http.post<Rapport>(`${this.apiUrl}/upload`, formData);
  }

  getMyRapports(): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(`${this.apiUrl}/my-reports`);
  }

  getRapportById(id: number): Observable<Rapport> {
    return this.http.get<Rapport>(`${this.apiUrl}/${id}`);
  }

  getAllRapports(): Observable<Rapport[]> {
    return this.http.get<Rapport[]>(this.apiUrl);
  }
}
