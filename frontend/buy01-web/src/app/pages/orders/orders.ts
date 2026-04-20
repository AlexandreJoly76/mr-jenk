import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService, Order, Page } from '../../services/order.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './orders.html',
  styleUrls: ['./orders.css']
})
export class OrdersComponent implements OnInit {
  orders = signal<Order[]>([]);
  isSellerView = signal<boolean>(false);
  statusFilter = signal<string>('');
  keywordFilter = signal<string>('');
  loading = signal<boolean>(false);

  constructor(private orderService: OrderService, public userService: UserService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading.set(true);
    const params = {
      status: this.statusFilter(),
      keyword: this.keywordFilter()
    };

    const request = this.isSellerView() 
      ? this.orderService.getSellerOrders(params) 
      : this.orderService.getOrders(params);

    request.subscribe({
      next: (page) => {
        this.orders.set(page.content);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  toggleView(): void {
    this.isSellerView.set(!this.isSellerView());
    this.loadOrders();
  }

  cancelOrder(id: string): void {
    this.orderService.cancelOrder(id).subscribe(() => this.loadOrders());
  }

  redoOrder(id: string): void {
    this.orderService.redoOrder(id).subscribe(() => this.loadOrders());
  }

  deleteOrder(id: string): void {
    if (confirm('Supprimer cette commande de votre historique ?')) {
      this.orderService.deleteOrder(id).subscribe(() => this.loadOrders());
    }
  }

  updateStatus(id: string, status: string): void {
    this.orderService.updateOrderStatus(id, status).subscribe({
      next: () => this.loadOrders(),
      error: (err) => alert('Erreur lors du changement de statut')
    });
  }
}
