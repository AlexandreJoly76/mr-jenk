import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../../services/user.service';

export const guestGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);

  // On vérifie si l'utilisateur est DÉJÀ connecté
  if (userService.currentUser()) {
    // Si oui, on le renvoie vers l'accueil (ou dashboard)
    // On peut aussi afficher un petit toast/alerte si on veut
    return router.createUrlTree(['/']);
  }

  // Si non (il est null), on le laisse accéder à Login/Signup
  return true;
};
