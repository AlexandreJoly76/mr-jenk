import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserService } from './user.service';

// --- MODELS (Alignés avec ton Backend Java) ---

export interface CartItem {
  productId: string;
  productName: string;
  price: number;
  quantity: number;
  imageUrl?: string;
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
  totalPrice: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private http = inject(HttpClient);
  private userService = inject(UserService); // On a besoin du User pour son ID

  // URL Gateway (8080) -> Order Service
  // Note : J'utilise https comme dans ton UserService
  private baseUrl = 'https://localhost:8080/order-service/api/cart';

  /**
   * Récupère l'ID de l'utilisateur connecté via le UserService.
   * Si pas connecté, renvoie une chaîne vide ou gère l'erreur.
   */
  private get currentUserId(): string {
    // 1. On regarde le signal (état actuel)
    const user = this.userService.currentUser();
    if (user && user.id) {
      return user.id;
    }

    // 2. Si le signal est vide (ex: refresh page), on tente de relire le token
    const tokenInfo = this.userService.getUserInfoFromToken();
    if (tokenInfo && tokenInfo.id) {
      return tokenInfo.id;
    }

    // 3. Sinon, l'utilisateur n'est pas connecté
    console.warn("Utilisateur non connecté, impossible d'accéder au panier.");
    return '';
  }

  // --- MÉTHODES API ---

  // GET : Récupérer le panier
  getCart(): Observable<Cart> {
    const uid = this.currentUserId;
    return this.http.get<Cart>(`${this.baseUrl}/${uid}`);
  }

  // POST : Ajouter un item
  addToCart(item: CartItem): Observable<Cart> {
    const uid = this.currentUserId;
    return this.http.post<Cart>(`${this.baseUrl}/${uid}/add`, item);
  }

  // DELETE : Retirer un item spécifique
  removeFromCart(productId: string): Observable<Cart> {
    const uid = this.currentUserId;
    return this.http.delete<Cart>(`${this.baseUrl}/${uid}/remove/${productId}`);
  }

  // DELETE : Vider tout le panier
  clearCart(): Observable<void> {
    const uid = this.currentUserId;
    return this.http.delete<void>(`${this.baseUrl}/${uid}/clear`);
  }
}
