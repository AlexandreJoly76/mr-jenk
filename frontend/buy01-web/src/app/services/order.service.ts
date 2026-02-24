import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderItem {
  productId: string;
  productName: string;
  priceAtPurchase: number;
  quantity: number;
  sellerId: string;
}

export interface Order {
  id: string;
  userId: string;
  items: OrderItem[];
  totalAmount: number;
  status: string;
  paymentMethod: string;
  shippingAddress: string;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'https://localhost:8080/order-service/api/orders';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({ 'Authorization': `Bearer ${localStorage.getItem('token')}` });
  }

  getOrders(params: any): Observable<Page<Order>> {
    let httpParams = new HttpParams();
    Object.keys(params).forEach(key => {
      if (params[key]) httpParams = httpParams.append(key, params[key]);
    });
    return this.http.get<Page<Order>>(this.apiUrl, { headers: this.getHeaders(), params: httpParams });
  }

  getSellerOrders(params: any): Observable<Page<Order>> {
    let httpParams = new HttpParams();
    Object.keys(params).forEach(key => {
      if (params[key]) httpParams = httpParams.append(key, params[key]);
    });
    return this.http.get<Page<Order>>(`${this.apiUrl}/seller`, { headers: this.getHeaders(), params: httpParams });
  }

  checkout(shippingAddress: string, paymentMethod: string): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/checkout`, { shippingAddress, paymentMethod }, { headers: this.getHeaders() });
  }

  cancelOrder(orderId: string): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/${orderId}/cancel`, {}, { headers: this.getHeaders() });
  }

  redoOrder(orderId: string): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/${orderId}/redo`, {}, { headers: this.getHeaders() });
  }

  deleteOrder(orderId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${orderId}`, { headers: this.getHeaders() });
  }
}
