import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-login',
  standalone: true, // Par défaut maintenant, mais je le reprécise
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);

  message = signal<string>('');

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  onSubmit() {
    if (this.loginForm.invalid) return;

    const credentials = this.loginForm.getRawValue();

    this.userService.login(credentials).subscribe({
      next: (token) => {
        // 1. On stocke le token dans le navigateur
        localStorage.setItem('token', token);

        // 2. On affiche un message (optionnel car on redirige vite)
        this.message.set('Connexion réussie ! Redirection...');

        // 3. On redirige vers la page d'accueil (ou dashboard)
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 1000);
      },
      error: (err) => {
        console.error(err);
        this.message.set('Email ou mot de passe incorrect.');
      }
    });
  }
}
