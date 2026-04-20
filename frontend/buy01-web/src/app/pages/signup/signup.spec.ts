import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Signup } from './signup';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from '../../services/user.service';
import { Media } from '../../services/media';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('Signup', () => {
  let component: Signup;
  let fixture: ComponentFixture<Signup>;
  let userService: UserService;
  let mediaService: Media;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Signup, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        UserService,
        Media
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Signup);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    mediaService = TestBed.inject(Media);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expect(component.signupForm.valid).toBeFalsy();
  });

  it('should validate email format', () => {
    const email = component.signupForm.controls.email;
    email.setValue('invalid-email');
    expect(email.hasError('email')).toBeTruthy();
    
    email.setValue('valid@example.com');
    expect(email.hasError('email')).toBeFalsy();
  });

  it('should validate password length', () => {
    const password = component.signupForm.controls.password;
    password.setValue('123');
    expect(password.hasError('minlength')).toBeTruthy();
    
    password.setValue('123456');
    expect(password.hasError('minlength')).toBeFalsy();
  });

  it('should call userService.register on valid form submission without avatar', () => {
    const registerSpy = spyOn(userService, 'register').and.returnValue(of({ id: '1', name: 'Test', email: 'test@example.com', role: 'CLIENT' }));
    
    component.signupForm.setValue({
      name: 'Test User',
      email: 'test@example.com',
      password: 'password123',
      role: 'CLIENT'
    });
    
    component.onSubmit();
    
    expect(registerSpy).toHaveBeenCalled();
  });

  it('should call mediaService.upload then userService.register when SELLER has avatar', () => {
    const mediaSpy = spyOn(mediaService, 'upload').and.returnValue(of({ id: 'img1', name: 'avatar.png' }));
    const registerSpy = spyOn(userService, 'register').and.returnValue(of({ id: '1', name: 'Seller', email: 'seller@example.com', role: 'SELLER' }));
    
    component.signupForm.setValue({
      name: 'Seller User',
      email: 'seller@example.com',
      password: 'password123',
      role: 'SELLER'
    });
    
    component.selectedFile = new File([''], 'avatar.png', { type: 'image/png' });
    
    component.onSubmit();
    
    expect(mediaSpy).toHaveBeenCalled();
    expect(registerSpy).toHaveBeenCalled();
    const request = registerSpy.calls.mostRecent().args[0];
    expect(request.avatar).toBe('img1');
  });

  it('should navigate to login on success', fakeAsync(() => {
    spyOn(userService, 'register').and.returnValue(of({ id: '1', name: 'Test', email: 'test@example.com', role: 'CLIENT' }));
    const navigateSpy = spyOn(router, 'navigate');
    
    component.signupForm.setValue({
      name: 'Test User',
      email: 'test@example.com',
      password: 'password123',
      role: 'CLIENT'
    });
    
    component.onSubmit();
    
    tick(2000);
    
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  }));

  it('should handle conflict error (409)', () => {
    spyOn(userService, 'register').and.returnValue(throwError(() => ({ status: 409 })));
    
    component.signupForm.setValue({
      name: 'Test User',
      email: 'test@example.com',
      password: 'password123',
      role: 'CLIENT'
    });
    
    component.onSubmit();
    
    expect(component.message()).toBe('Cet email ou username est déjà utilisé !');
  });

  it('should handle generic error', () => {
    spyOn(userService, 'register').and.returnValue(throwError(() => new Error('Failed')));
    
    component.signupForm.setValue({
      name: 'Test User',
      email: 'test@example.com',
      password: 'password123',
      role: 'CLIENT'
    });
    
    component.onSubmit();
    
    expect(component.message()).toBe('Erreur lors de l’inscription.');
  });

  it('should handle avatar upload failure', () => {
    spyOn(mediaService, 'upload').and.returnValue(throwError(() => new Error('Upload failed')));
    
    component.signupForm.setValue({
      name: 'Seller User',
      email: 'seller@example.com',
      password: 'password123',
      role: 'SELLER'
    });
    component.selectedFile = new File([''], 'avatar.png', { type: 'image/png' });
    
    component.onSubmit();
    
    expect(component.message()).toBe("Format invalide pour l'avatar ou taille supérieure à 2MB.");
  });

  it('should update selectedFile on file selection', () => {
    const file = new File([''], 'test.png', { type: 'image/png' });
    const event = { target: { files: [file] } };
    
    component.onFileSelected(event);
    
    expect(component.selectedFile).toBe(file);
  });
});
