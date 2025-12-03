import {inject, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Product {
  id?: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId:string;
  imageId?:string;
}

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl='http://localhost:8080/product-service/api/products';

  private http=inject(HttpClient);

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  // --- AJOUT ---
  createProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  getProductsBySeller(userId: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/seller/${userId}`);
  }

  // Supprimer un produit
  deleteProduct(productId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${productId}`);
  }

}
