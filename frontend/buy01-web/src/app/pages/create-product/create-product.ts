import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Media } from '../../services/media';
import {UserService} from '../../services/user.service';
import { forkJoin } from 'rxjs';
import {Router} from '@angular/router'; // <--- IMPORT IMPORTANT

@Component({
  selector: 'app-create-product',
  imports: [ReactiveFormsModule],
  templateUrl: './create-product.html',
  styleUrl: './create-product.css'
})
export class CreateProduct {
  private fb = inject(FormBuilder);
  private productService = inject(ProductService);
  private mediaService = inject(Media);
  private userService = inject(UserService);
  private router = inject(Router);

  message = signal<string>('');

// CHangement : On stocke maintenant un tableau de fichiers
  selectedFiles: File[] = [];

  productForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0.1)]],
    quantity: [1, [Validators.required, Validators.min(1)]], // Ajout
  });

  currentUser: any = null;

  ngOnInit() {
    // Au chargement, on récupère les infos du token
    this.currentUser = this.userService.getUserInfoFromToken();

    if (!this.currentUser) {
      this.message.set("Attention : Vous n'êtes pas connecté !");
    }
  }

// Nouvelle méthode de sélection
  onFileSelected(event: any) {
    // event.target.files est un "FileList", on le convertit en vrai tableau []
    this.selectedFiles = Array.from(event.target.files);
  }

  onSubmit() {
    if (this.productForm.invalid || this.selectedFiles.length === 0 || !this.currentUser) {
      this.message.set("Formulaire invalide.");
      return;
    }

    this.message.set(`Upload de ${this.selectedFiles.length} images en cours...`);

    // 1. On prépare toutes les requêtes d'upload
    // On transforme chaque fichier (File) en un appel au service (Observable)
    const uploadRequests = this.selectedFiles.map(file => this.mediaService.upload(file));

    // 2. forkJoin lance tout en parallèle et attend la fin
    forkJoin(uploadRequests).subscribe({
      next: (responses) => {
        // 'responses' est un tableau contenant les réponses du Media Service
        // On extrait juste les IDs
        const uploadedImageIds = responses.map(res => res.id);
        console.log("Tous les uploads sont finis. IDs:", uploadedImageIds);

        // 3. On crée le produit avec la liste d'IDs
        const newProduct = {
          ...this.productForm.getRawValue(),
          imageIds: uploadedImageIds, // <-- La nouvelle liste
          userId: this.currentUser.id
        };

        this.createProductInBackend(newProduct);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error(err);
        this.message.set("Erreur lors de l'upload des images.");
      }
    });
  }

  private createProductInBackend(productData: any) {
    // ... inchangé, sauf qu'on remet à zéro la liste des fichiers
    this.productService.createProduct(productData).subscribe({
      next: (prod) => {
        this.message.set(`Produit "${prod.name}" créé avec ${prod.imageIds?.length} images !`);
        this.productForm.reset();
        this.selectedFiles = []; // Reset de la sélection
      },
      error: (err) => {
        console.error(err);
        this.message.set("Erreur lors de la création du produit.");
      }
    });
  }
}
