export interface User {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  role: 'ETUDIANT' | 'COORDINATEUR' | 'DA';
  filiere?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  nom: string;
  prenom: string;
  role: string;
  filiere?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  nom: string;
  prenom: string;
  role: string;
  filiere?: string;
}
