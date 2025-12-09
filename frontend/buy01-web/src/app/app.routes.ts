import { Routes } from '@angular/router';
import { Signup } from './pages/signup/signup';

import {CreateProduct} from './pages/create-product/create-product';
import {Login} from './pages/login/login';
import {SellerDashboard} from './pages/seller-dashboard/seller-dashboard';
import {Home} from './pages/home/home';
import {sellerGuard} from './core/guards/seller-guard';
import {guestGuard} from './core/guards/guest-guard';

export const routes: Routes = [
  // Pour l'instant, on laisse la liste produit sur l'accueil (path vide)
  // Mais idéalement on déplacerait la liste produit dans son propre composant 'Home'
  { path: '',component:Home},
  { path: 'signup', component: Signup, canActivate:[guestGuard] },
  { path: 'login', component: Login, canActivate:[guestGuard] },
  { path: 'sell', component: CreateProduct, canActivate:[sellerGuard] },
  { path: 'dashboard', component: SellerDashboard, canActivate:[sellerGuard] },
  { path:'**', redirectTo: '' } // Redirection pour les routes inconnues
];
