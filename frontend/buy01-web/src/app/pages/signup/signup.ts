import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, UserRequest } from '../../services/user.service';
import { Media } from '../../services/media'; // <-- IMPORT

@Component({
  selector: 'app-signup',
  imports: [ReactiveFormsModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private mediaService = inject(Media); // <-- INJECTION
  private router = inject(Router);

  message = signal<string>('');
  selectedFile: File | null = null; // Pour stocker l'avatar

  signupForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['CLIENT' as 'CLIENT' | 'SELLER', Validators.required]
  });

  // Détecter le fichier choisi
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  onSubmit() {
    if (this.signupForm.invalid) return;

    const formValues = this.signupForm.getRawValue();
    const role = formValues.role as 'CLIENT' | 'SELLER';

    // LOGIQUE CONDITIONNELLE
    // Si c'est un VENDEUR et qu'il a choisi un fichier -> On upload d'abord
    if (role === 'SELLER' && this.selectedFile) {

      this.message.set("Upload de l'avatar en cours...");

      this.mediaService.upload(this.selectedFile).subscribe({
        next: (mediaResponse) => {
          // L'upload est fini, on a l'ID de l'image
          this.registerUser(formValues, mediaResponse.id);
        },
        error: (err) => this.message.set("Erreur lors de l'upload de l'avatar.")
      });

    } else {
      // Si c'est un CLIENT ou un Vendeur sans avatar -> On inscrit direct (avatar = null)
      this.registerUser(formValues, undefined);
    }
  }

  // Méthode privée pour finaliser l'inscription
  private registerUser(formValues: any, avatarId: string | undefined) {
    const request: UserRequest = {
      name: formValues.name,
      email: formValues.email,
      password: formValues.password,
      role: formValues.role as 'CLIENT' | 'SELLER',
      avatar: avatarId // On ajoute l'ID de l'image ici
    };

    this.userService.register(request).subscribe({
      next: (user) => {
        this.message.set(`Succès ! ${user.name} inscrit.`);
        this.signupForm.reset({ role: 'CLIENT' });
        this.selectedFile = null;
        // Optionnel : Redirection vers le login après 2 sec
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        // Gestion propre de l'erreur "Email déjà pris" (grâce au backend qu'on a fait avant)
        if (err.status === 409) {
          this.message.set("Cet email est déjà utilisé !");
        } else {
          this.message.set("Erreur lors de l'inscription.");
        }
      }
    });
  }
}
