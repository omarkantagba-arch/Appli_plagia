import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Soutenance } from '../models/theme.model';
import { SoutenanceDTO } from '../models/soutenance.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SoutenanceService {
  private apiUrl = `${environment.apiUrl}/soutenances`;

  constructor(private http: HttpClient) {}

  getAllSoutenances(): Observable<Soutenance[]> {
    return this.http.get<Soutenance[]>(this.apiUrl);
  }

  getMySoutenance(): Observable<Soutenance> {
    return this.http.get<Soutenance>(`${this.apiUrl}/my-soutenance`);
  }

  createSoutenance(dto: SoutenanceDTO): Observable<Soutenance> {
    return this.http.post<Soutenance>(this.apiUrl, dto);
  }

  updateSoutenance(id: number, dto: SoutenanceDTO): Observable<Soutenance> {
    return this.http.put<Soutenance>(`${this.apiUrl}/${id}`, dto);
  }

  deleteSoutenance(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
