export interface Theme {
  id: number;
  titre: string;
  description: string;
  filiere: string;
  statut: 'EN_ATTENTE' | 'PROPOSE_VALIDATION' | 'PROPOSE_REJET' | 'VALIDE' | 'REJETE';
  plagiatScore?: number;
  commentaire?: string;
  etudiant: any;
  coordinateur?: any;
  validateur?: any;
  encadrant?: any;
  createdAt: Date;
  updatedAt: Date;
}

export interface Rapport {
  id: number;
  fichierUrl: string;
  textContent?: string;
  plagiatScore?: number;
  statut: 'EN_ANALYSE' | 'VALIDE' | 'REJETE' | 'EN_EXAMEN';
  version: number;
  theme: Theme;
  etudiant: any;
  createdAt: Date;
  updatedAt: Date;
}

export interface Soutenance {
  id: number;
  date: string;
  heure: string;
  salle: string;
  jury: string[];
  theme: Theme;
  etudiant: any;
  note?: number;
  appreciation?: string;
}

export interface Notification {
  id: number;
  type: string;
  titre: string;
  message: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  lu: boolean;
  user: any;
  createdAt: Date;
}
