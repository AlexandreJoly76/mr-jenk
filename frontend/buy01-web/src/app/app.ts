import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Product, ProductService } from './services/product.service';
import {RouterLink, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-root',
  // RÈGLE : Pas de 'standalone: true' explicite (c'est le défaut)
  // RÈGLE : OnPush pour la performance
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet,RouterLink], // Plus besoin de CommonModule grâce au Control Flow (@for, @if)
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  // RÈGLE : Injection de dépendance via inject()
  private productService = inject(ProductService);

  // RÈGLE : Gestion d'état via Signal
  // On initialise avec un tableau vide
  products = signal<Product[]>([]);

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        // RÈGLE : Mise à jour du signal via .set()
        this.products.set(data);
        console.log('Produits reçus (Signal mis à jour):', this.products());
      },
      error: (err) => {
        console.error('Erreur de chargement:', err);
      }
    });
  }
}
