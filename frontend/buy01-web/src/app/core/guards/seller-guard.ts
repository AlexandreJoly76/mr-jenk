import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../../services/user.service';

export const sellerGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);

  // On récupère l'utilisateur actuel (depuis le Signal)
  const user = userService.currentUser();

  // 1. Est-ce qu'il est connecté ?
  if (!user) {
    // Non connecté -> On le renvoie vers le Login
    return router.createUrlTree(['/login']);
  }

  // 2. Est-ce qu'il est Vendeur ?
  if (user.role === 'SELLER') {
    // C'est bon, on laisse passer
    return true;
  }

  // 3. Connecté mais pas Vendeur (donc Client) -> Retour à l'accueil
  // On pourrait aussi afficher une page "Accès Interdit 403"
  return router.createUrlTree(['/']);
};
