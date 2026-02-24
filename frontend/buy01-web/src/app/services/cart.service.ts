import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CartItem {
  productId: string;
  productName: string;
  price: number;
  quantity: number;
  sellerId: string;
}

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'https://localhost:8080/cart-service/api/carts';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl, { headers: this.getHeaders() });
  }

  addToCart(productId: string, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(this.apiUrl, { productId, quantity }, { headers: this.getHeaders() });
  }

  updateQuantity(productId: string, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(this.apiUrl, { productId, quantity }, { headers: this.getHeaders() });
  }

  removeFromCart(productId: string): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/${productId}`, { headers: this.getHeaders() });
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/clear`, { headers: this.getHeaders() });
  }
}
