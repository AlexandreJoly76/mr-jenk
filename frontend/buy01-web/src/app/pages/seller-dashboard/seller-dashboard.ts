import { Component, OnInit, inject, signal } from '@angular/core';
import { Product, ProductService } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { RouterLink } from '@angular/router';
import {Carousel} from '../../shared/carousel/carousel';

@Component({
  selector: 'app-seller-dashboard',
  imports: [RouterLink,Carousel], // Pour le lien "Ajouter un produit"
  templateUrl: './seller-dashboard.html',
  styleUrl: './seller-dashboard.css'
})
export class SellerDashboard implements OnInit {
  private productService = inject(ProductService);
  private userService = inject(UserService);

  myProducts = signal<Product[]>([]);
  currentUser: any = null;

  ngOnInit() {
    this.currentUser = this.userService.getUserInfoFromToken();

    if (this.currentUser) {
      this.loadMyProducts();
    }
  }

  loadMyProducts() {
    // On utilise l'ID du token pour charger NOS produits
    this.productService.getProductsBySeller(this.currentUser.id).subscribe({
      next: (data) => this.myProducts.set(data),
      error: (err) => console.error("Erreur chargement stock", err)
    });
  }

  delete(productId: string | undefined) {
    if (!productId) return;

    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.productService.deleteProduct(productId).subscribe({
        next: () => {
          // On recharge la liste après suppression
          this.loadMyProducts();
        },
        error: (err) => alert("Erreur lors de la suppression")
      });
    }
  }
}
