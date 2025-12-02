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

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  // On passe par le Gateway (port 8080) -> User Service
  private apiUrl = 'http://localhost:8080/user-service/api/users/register';

  register(user: UserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.apiUrl, user);
  }
}
