import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MediaResponse {
  id: string;
  name: string;
  // on ignore le champ 'data' qui est trop lourd, on veut juste l'ID
}

@Injectable({
  providedIn: 'root'
})
export class Media {
  private http = inject(HttpClient);
  // URL vers le Media Service via Gateway
  private apiUrl = 'https://localhost:8080/media-service/api/media';

  upload(file: File): Observable<MediaResponse> {
    // Pour envoyer un fichier, on doit utiliser FormData, pas du JSON classique
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<MediaResponse>(this.apiUrl, formData);
  }
}
