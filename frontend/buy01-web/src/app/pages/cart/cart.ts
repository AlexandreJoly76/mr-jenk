import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService, Cart, CartItem } from '../../services/cart.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  totalPrice: number = 0;
  errorMessage: string = '';

  constructor(private cartService: CartService, private router: Router) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart = cart;
        this.calculateTotal();
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors du chargement du panier.';
        console.error(err);
      }
    });
  }

  calculateTotal(): void {
    if (this.cart) {
      this.totalPrice = this.cart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    }
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity < 1) return;
    this.cartService.updateQuantity(productId, quantity).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.calculateTotal();
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors de la mise à jour de la quantité.';
      }
    });
  }

  removeItem(productId: string): void {
    this.cartService.removeFromCart(productId).subscribe({
      next: (cart) => {
        this.cart = cart;
        this.calculateTotal();
      },
      error: (err) => {
        this.errorMessage = 'Erreur lors de la suppression de l\'article.';
      }
    });
  }

  checkout(): void {
    // For now, redirect to a placeholder or stay on page if order service UI is not ready
    // this.router.navigate(['/checkout']);
    alert('Redirection vers le paiement (à implémenter)...');
  }
}
