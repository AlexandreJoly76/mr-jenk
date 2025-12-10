import {Component, inject, OnInit, signal} from '@angular/core';
import {Product, ProductService} from '../../services/product.service';
import {Carousel} from '../../shared/carousel/carousel';

@Component({
  selector: 'app-home',
  imports: [
    Carousel
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit{

  private productService = inject(ProductService);

  // RÈGLE : Gestion d'état via Signal
  // On initialise avec un tableau vide
  products = signal<Product[]>([]);

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        // RÈGLE : Mise à jour du signal via .set()
        this.products.set(data);
        console.log(data);
        console.log('Produits reçus (Signal mis à jour):', this.products());
      },
      error: (err) => {
        console.error('Erreur de chargement:', err);
      }
    });
  }

}
