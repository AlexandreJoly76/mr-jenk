import { Component, OnInit } from '@angular/core';
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
  userStats: UserStats | null = null;
  sellerStats: SellerStats | null = null;
  isSeller: boolean = false;

  constructor(private statsService: StatsService, public userService: UserService) {}

  ngOnInit(): void {
    this.isSeller = this.userService.currentUser()?.role === 'SELLER';
    this.loadStats();
  }

  loadStats(): void {
    this.statsService.getUserStats().subscribe(stats => this.userStats = stats);
    if (this.isSeller) {
      this.statsService.getSellerStats().subscribe(stats => this.sellerStats = stats);
    }
  }
}
