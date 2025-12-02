import { Routes } from '@angular/router';
import { Signup } from './pages/signup/signup';
import { App } from './app';
import {CreateProduct} from './pages/create-product/create-product';

export const routes: Routes = [
  // Pour l'instant, on laisse la liste produit sur l'accueil (path vide)
  // Mais idéalement on déplacerait la liste produit dans son propre composant 'Home'
  { path: 'signup', component: Signup },
  { path: 'sell', component: CreateProduct }
];
