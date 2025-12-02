import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, UserRequest } from '../../services/user.service';

@Component({
  selector: 'app-signup',
  imports: [ReactiveFormsModule], // RÈGLE : Import nécessaire pour [formGroup]
  templateUrl: './signup.html',
  styleUrl: './signup.css'
})
export class Signup {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);

  // Message de succès ou erreur géré par un Signal
  message = signal<string>('');

  // Création du formulaire avec validations
  signupForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    role: ['CLIENT' as 'CLIENT' | 'SELLER', Validators.required]
  });

  onSubmit() {
    if (this.signupForm.invalid) return;

    const formValues=this.signupForm.getRawValue();

    // On récupère les valeurs du formulaire
    const request: UserRequest = {
      name: formValues.name,
      email: formValues.email,
      password: formValues.password,
      role: formValues.role
    };

    this.userService.register(request).subscribe({
      next: (user) => {
        this.message.set(`Succès ! ${user.name} inscrit en tant que ${user.role}.`);
        // Reset du formulaire
        this.signupForm.reset({ role: 'CLIENT' }); // On garde le rôle par défaut au reset
      },
      error: (err) => {
        console.error(err);
        this.message.set('Erreur lors de l\'inscription.');
      }
    });
  }
}
