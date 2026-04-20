import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Login } from './login';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from '../../services/user.service';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let userService: UserService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        UserService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expect(component.loginForm.valid).toBeFalsy();
  });

  it('should validate email format', () => {
    const email = component.loginForm.controls.email;
    email.setValue('invalid-email');
    expect(email.hasError('email')).toBeTruthy();
    
    email.setValue('valid@example.com');
    expect(email.hasError('email')).toBeFalsy();
  });

  it('should validate required fields', () => {
    const email = component.loginForm.controls.email;
    const password = component.loginForm.controls.password;
    
    expect(email.hasError('required')).toBeTruthy();
    expect(password.hasError('required')).toBeTruthy();
  });

  it('should call userService.login on valid form submission', () => {
    const loginSpy = spyOn(userService, 'login').and.returnValue(of('fake-token'));
    
    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(loginSpy).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
  });

  it('should navigate to home on login success', fakeAsync(() => {
    spyOn(userService, 'login').and.returnValue(of('fake-token'));
    const navigateSpy = spyOn(router, 'navigate');
    
    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'password123'
    });
    
    component.onSubmit();
    
    tick(1000); // Because of setTimeout in onSubmit
    
    expect(component.message()).toBe('Connexion réussie ! Redirection...');
    expect(navigateSpy).toHaveBeenCalledWith(['/']);
  }));

  it('should show error message on login failure', () => {
    spyOn(userService, 'login').and.returnValue(throwError(() => new Error('Login failed')));
    
    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'password123'
    });
    
    component.onSubmit();
    
    expect(component.message()).toBe('Email ou mot de passe incorrect.');
  });

  it('should not call userService.login if form is invalid', () => {
    const loginSpy = spyOn(userService, 'login');
    component.onSubmit();
    expect(loginSpy).not.toHaveBeenCalled();
  });
});
