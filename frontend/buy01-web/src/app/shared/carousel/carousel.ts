import { Component, Input, signal, computed } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage], // Nécesaire pour ngClass/ngStyle si besoin
  templateUrl: './carousel.html',
  styleUrl: './carousel.css'
})
export class Carousel {
  // Entrée : la liste des IDs des images à afficher
  @Input({ required: true }) imageIds: string[] = [];

  // État : l'index de l'image actuellement visible (commence à 0)
  currentIndex = signal(0);

  // URL de base pour les images
  private baseUrl = 'https://localhost:8080/media-service/api/media/';

  // Computed : Calcule l'URL complète de l'image courante
  currentImageUrl = computed(() => {
    if (this.imageIds.length === 0) return null;
    // On récupère l'ID à l'index actuel
    const currentId = this.imageIds[this.currentIndex()];
    return this.baseUrl + currentId;
  });

  // Méthode pour aller à l'image suivante
  next() {
    this.currentIndex.update(index => {
      // Si on est à la dernière image, on retourne à la première (0), sinon on avance (+1)
      return index === this.imageIds.length - 1 ? 0 : index + 1;
    });
  }

  // Méthode pour aller à l'image précédente
  previous() {
    this.currentIndex.update(index => {
      // Si on est à la première image (0), on va à la dernière, sinon on recule (-1)
      return index === 0 ? this.imageIds.length - 1 : index - 1;
    });
  }
}
