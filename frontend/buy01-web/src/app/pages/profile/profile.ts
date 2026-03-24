import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatsService, UserStats, SellerStats } from '../../services/stats.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css']
})
export class ProfileComponent implements OnInit {
  private statsService = inject(StatsService);
  public userService = inject(UserService);

  // Utilisation de signaux pour une réactivité parfaite
  userStats = signal<UserStats | null>(null);
  sellerStats = signal<SellerStats | null>(null);
  loading = signal<boolean>(false);

  // isSeller est automatiquement recalculé si l'utilisateur change
  isSeller = computed(() => this.userService.currentUser()?.role === 'SELLER');

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.loading.set(true);
    
    // Chargement des stats acheteur
    this.statsService.getUserStats().subscribe({
      next: (stats) => {
        this.userStats.set(stats);
        if (!this.isSeller()) this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });

    // Chargement des stats vendeur si nécessaire
    if (this.isSeller()) {
      this.statsService.getSellerStats().subscribe({
        next: (stats) => {
          this.sellerStats.set(stats);
          this.loading.set(false);
        },
        error: () => this.loading.set(false)
      });
    }
  }
}
