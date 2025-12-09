import { Component, OnInit, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { Product, ProductService } from './services/product.service';
import {RouterLink, RouterOutlet} from '@angular/router';
import {NgOptimizedImage} from '@angular/common';
import {UserService} from './services/user.service';

@Component({
  selector: 'app-root',
  // RÈGLE : Pas de 'standalone: true' explicite (c'est le défaut)
  // RÈGLE : OnPush pour la performance
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, RouterLink, NgOptimizedImage], // Plus besoin de CommonModule grâce au Control Flow (@for, @if)
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  // RÈGLE : Injection de dépendance via inject()
  public userService=inject(UserService);

}
