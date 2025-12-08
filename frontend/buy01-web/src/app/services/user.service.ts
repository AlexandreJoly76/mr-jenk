import {Injectable, inject, signal} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {Router} from '@angular/router';

// Le format de données qu'on envoie au backend
export interface UserRequest {
  name: string;      // Alignement avec le backend (était username)
  email: string;
  password: string;  // Ajout
  role: 'CLIENT' | 'SELLER'; // Ajout (Enum strict)
  avatar?: string;
}

// Le format de réponse (avec l'ID généré par Mongo)
export interface UserResponse {
  id: string;
  name: string;
  email: string;
  role:string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private router = inject(Router);
  // On passe par le Gateway (port 8080) -> User Service
  private registerUrl = 'https://localhost:8080/user-service/api/users/register';
  private loginUrl = 'https://localhost:8080/user-service/api/users/login'; // Nouvelle URL

  currentUser = signal<any>(null);

  constructor() {
    // Au démarrage de l'appli, on vérifie si un token existe déjà
    this.restoreUserFromToken();
  }

  // Méthode pour lire le token et mettre à jour le signal
  private restoreUserFromToken() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.currentUser.set(payload); // On met les infos dans le signal
      } catch (e) {
        console.error("Token invalide", e);
        this.logout();
      }
    }
  }

  register(user: UserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.registerUrl, user);
  }

  login(credentials: LoginRequest): Observable<string> {
    return this.http.post(this.loginUrl, credentials, { responseType: 'text' }).pipe(
      // Ce TAP est vital pour la mise à jour sans refresh
      tap((token) => {
        localStorage.setItem('token', token);
        this.restoreUserFromToken(); // <--- C'est ça qui réveille le Header !
      })
    );
  }

  getUserInfoFromToken(): any {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      // Le JWT a 3 parties séparées par des points. La 2ème partie est le "payload" (les données).
      const payload = token.split('.')[1];
      // On décode du Base64 et on parse le JSON
      return JSON.parse(atob(payload));
    } catch (e) {
      console.error("Erreur décodage token", e);
      return null;
    }
  }

  // Méthode pour savoir si on est connecté
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  logout() {
    localStorage.removeItem('token');
    this.currentUser.set(null); // On vide le signal
    this.router.navigate(['/login']); // Redirection
  }
}
