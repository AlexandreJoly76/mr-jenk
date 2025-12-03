import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Le format de données qu'on envoie au backend
export interface UserRequest {
  name: string;      // Alignement avec le backend (était username)
  email: string;
  password: string;  // Ajout
  role: 'CLIENT' | 'SELLER'; // Ajout (Enum strict)
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
  // On passe par le Gateway (port 8080) -> User Service
  private registerUrl = 'http://localhost:8080/user-service/api/users/register';
  private loginUrl = 'http://localhost:8080/user-service/api/users/login'; // Nouvelle URL

  register(user: UserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.registerUrl, user);
  }

  // --- AJOUT LOGIN ---
  login(credentials: LoginRequest): Observable<string> {
    // Note : On précise { responseType: 'text' } car le backend renvoie une String brute, pas un objet JSON
    return this.http.post(this.loginUrl, credentials, { responseType: 'text' });
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

  // Méthode pour se déconnecter
  logout() {
    localStorage.removeItem('token');
  }
}
