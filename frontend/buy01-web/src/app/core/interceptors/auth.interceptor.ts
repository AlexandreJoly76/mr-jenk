import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 1. Récupérer le token
  const token = localStorage.getItem('token');

  // 2. Si le token existe, on clone la requête pour ajouter le header
  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest);
  }

  // 3. Sinon, on laisse passer la requête telle quelle
  return next(req);
};
