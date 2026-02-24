import { Routes } from '@angular/router';
import { Signup } from './pages/signup/signup';

import {CreateProduct} from './pages/create-product/create-product';
import {Login} from './pages/login/login';
import {SellerDashboard} from './pages/seller-dashboard/seller-dashboard';
import {Home} from './pages/home/home';
import {CartComponent} from './pages/cart/cart';
import {OrdersComponent} from './pages/orders/orders';
import {ProfileComponent} from './pages/profile/profile';
import {sellerGuard} from './core/guards/seller-guard';
import {guestGuard} from './core/guards/guest-guard';

export const routes: Routes = [
  { path: '',component:Home},
  { path: 'signup', component: Signup, canActivate:[guestGuard] },
  { path: 'login', component: Login, canActivate:[guestGuard] },
  { path: 'sell', component: CreateProduct, canActivate:[sellerGuard] },
  { path: 'dashboard', component: SellerDashboard, canActivate:[sellerGuard] },
  { path: 'cart', component: CartComponent },
  { path: 'orders', component: OrdersComponent },
  { path: 'profile', component: ProfileComponent },
  { path:'**', redirectTo: '' }
];
