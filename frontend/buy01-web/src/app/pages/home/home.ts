import {Component, inject, OnInit, signal, computed} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Product, ProductService} from '../../services/product.service';
import {CartService} from '../../services/cart.service';
import {Carousel} from '../../shared/carousel/carousel';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    Carousel,
    FormsModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit{

  private productService = inject(ProductService);
  private cartService = inject(CartService);
  protected readonly Number = Number;

  // État des produits
  products = signal<Product[]>([]);
  
  // États des filtres
  searchTerm = signal<string>('');
  selectedCategory = signal<string>('');
  minPrice = signal<number | null>(null);
  maxPrice = signal<number | null>(null);

  // Catégories uniques extraites des produits
  categories = computed(() => {
    const cats = this.products().map(p => p.category).filter(c => !!c);
    return [...new Set(cats)].sort();
  });

  // Liste filtrée calculée automatiquement
  filteredProducts = computed(() => {
    return this.products().filter(product => {
      const matchesSearch = !this.searchTerm() || 
        product.name.toLowerCase().includes(this.searchTerm().toLowerCase()) ||
        product.description.toLowerCase().includes(this.searchTerm().toLowerCase());
      
      const matchesCategory = !this.selectedCategory() || 
        product.category === this.selectedCategory();
      
      const matchesMinPrice = this.minPrice() === null || 
        product.price >= (this.minPrice() ?? 0);
      
      const matchesMaxPrice = this.maxPrice() === null || 
        product.price <= (this.maxPrice() ?? Infinity);

      return matchesSearch && matchesCategory && matchesMinPrice && matchesMaxPrice;
    });
  });

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (data) => {
        this.products.set(data);
      },
      error: (err) => {
        console.error('Erreur de chargement:', err);
      }
    });
  }

  addToCart(productId: string): void {
    const product = this.products().find(p => p.id === productId);
    if (product && Number(product.quantity) <= 0) {
      alert('Désolé, ce produit est épuisé.');
      return;
    }

    this.cartService.addToCart(productId, 1).subscribe({
      next: () => {
        alert('Produit ajouté au panier !');
      },
      error: (err) => {
        console.error('Erreur lors de l\'ajout au panier:', err);
        alert('Erreur lors de l\'ajout au panier.');
      }
    });
  }
}
