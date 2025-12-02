import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Media } from '../../services/media';

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

  message = signal<string>('');

  // Variable pour stocker le fichier sélectionné par l'utilisateur
  selectedFile: File | null = null;

  productForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: ['', Validators.required],
    price: [0, [Validators.required, Validators.min(0.1)]],
    quantity: [1, [Validators.required, Validators.min(1)]], // Ajout
    userId: ['', Validators.required] // Renommé
  });

  // Méthode déclenchée quand l'utilisateur choisit un fichier
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
    if (this.productForm.invalid || !this.selectedFile) {
      this.message.set("Formulaire invalide ou image manquante.");
      return;
    }

    this.message.set("Upload de l'image en cours...");

    // 1. D'abord, on upload l'image
    this.mediaService.upload(this.selectedFile).subscribe({
      next: (mediaResponse) => {
        console.log("Image uploadée, ID:", mediaResponse.id);

        // 2. Ensuite, on crée le produit avec l'ID de l'image
        const newProduct = {
          ...this.productForm.getRawValue(),
          imageId: mediaResponse.id
        };

        this.createProductInBackend(newProduct);
      },
      error: (err) => {
        console.error(err);
        this.message.set("Erreur lors de l'upload de l'image.");
      }
    });
  }

  private createProductInBackend(productData: any) {
    this.productService.createProduct(productData).subscribe({
      next: (prod) => {
        this.message.set(`Produit "${prod.name}" créé avec succès !`);
        this.productForm.reset();
        this.selectedFile = null;
      },
      error: (err) => {
        console.error(err);
        this.message.set("Erreur lors de la création du produit.");
      }
    });
  }
}
