import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductSummary {
  productId: string;
  productName: string;
  count: number;
}

export interface UserStats {
  totalSpent: number;
  totalOrders: number;
  topProducts: ProductSummary[];
}

export interface SellerStats {
  totalRevenue: number;
  completedOrders: number;
  bestSellers: ProductSummary[];
}

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  private apiUrl = 'https://localhost:8080/order-service/api/orders/stats';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Authorization': `Bearer ${localStorage.getItem('token')}` });
  }

  getUserStats(): Observable<UserStats> {
    return this.http.get<UserStats>(`${this.apiUrl}/user`, { headers: this.getHeaders() });
  }

  getSellerStats(): Observable<SellerStats> {
    return this.http.get<SellerStats>(`${this.apiUrl}/seller`, { headers: this.getHeaders() });
  }
}
