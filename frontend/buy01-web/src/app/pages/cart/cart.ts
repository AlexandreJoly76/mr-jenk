import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService, Cart, CartItem } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class CartComponent implements OnInit {
  cart = signal<Cart | null>(null);
  errorMessage = signal<string>('');

  totalPrice = computed(() => {
    const currentCart = this.cart();
    if (!currentCart) return 0;
    return currentCart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  });

  constructor(
    private cartService: CartService, 
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart.set(cart);
      },
      error: (err) => {
        this.errorMessage.set('Erreur lors du chargement du panier.');
        console.error(err);
      }
    });
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity < 1) return;
    this.cartService.updateQuantity(productId, quantity).subscribe({
      next: (cart) => {
        this.cart.set(cart);
      },
      error: (err) => {
        this.errorMessage.set('Erreur lors de la mise à jour de la quantité.');
      }
    });
  }

  removeItem(productId: string): void {
    this.cartService.removeFromCart(productId).subscribe({
      next: (cart) => {
        this.cart.set(cart);
      },
      error: (err) => {
        this.errorMessage.set('Erreur lors de la suppression de l\'article.');
      }
    });
  }

  checkout(): void {
    const address = prompt('Entrez votre adresse de livraison :');
    if (!address) return;

    this.orderService.checkout(address, 'PAY_ON_DELIVERY').subscribe({
      next: () => {
        alert('Commande validée !');
        this.router.navigate(['/orders']);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage.set('Erreur lors de la validation de la commande.');
      }
    });
  }
}
