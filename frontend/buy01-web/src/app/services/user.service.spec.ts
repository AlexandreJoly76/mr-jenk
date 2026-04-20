import { TestBed } from '@angular/core/testing';
import { UserService, UserRequest, UserResponse, LoginRequest } from './user.service';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register a user', () => {
    const mockUser: UserRequest = {
      name: 'John Doe',
      email: 'john@example.com',
      password: 'password123',
      role: 'CLIENT'
    };
    const mockResponse: UserResponse = {
      id: '123',
      name: 'John Doe',
      email: 'john@example.com',
      role: 'CLIENT'
    };

    service.register(mockUser).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('https://localhost:8080/user-service/api/users/register');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should login a user and store token', () => {
    const credentials: LoginRequest = { email: 'john@example.com', password: 'password123' };
    // Mock payload: {"name":"John Doe","email":"john@example.com","role":"CLIENT"}
    const payload = btoa(JSON.stringify({ name: 'John Doe', email: 'john@example.com', role: 'CLIENT' }));
    const mockToken = `header.${payload}.signature`;

    service.login(credentials).subscribe(token => {
      expect(token).toBe(mockToken);
      expect(localStorage.getItem('token')).toBe(mockToken);
      expect(service.currentUser()).toEqual({ name: 'John Doe', email: 'john@example.com', role: 'CLIENT' });
    });

    const req = httpMock.expectOne('https://localhost:8080/user-service/api/users/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockToken);
  });

  it('should logout a user', () => {
    spyOn(router, 'navigate');
    localStorage.setItem('token', 'some-token');
    service.currentUser.set({ name: 'John' });

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(service.currentUser()).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should check if authenticated', () => {
    expect(service.isAuthenticated()).toBeFalse();
    localStorage.setItem('token', 'some-token');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should get user info from token', () => {
    const payload = { name: 'John Doe', email: 'john@example.com', role: 'CLIENT' };
    const encodedPayload = btoa(JSON.stringify(payload));
    const mockToken = `header.${encodedPayload}.signature`;
    localStorage.setItem('token', mockToken);

    const userInfo = service.getUserInfoFromToken();
    expect(userInfo).toEqual(payload);
  });

  it('should return null if token is invalid or missing', () => {
    expect(service.getUserInfoFromToken()).toBeNull();
    localStorage.setItem('token', 'invalid-token');
    expect(service.getUserInfoFromToken()).toBeNull();
  });

  it('should restore user from token on initialization', () => {
    const payload = { name: 'John Doe', email: 'john@example.com', role: 'CLIENT' };
    const encodedPayload = btoa(JSON.stringify(payload));
    const mockToken = `header.${encodedPayload}.signature`;
    localStorage.setItem('token', mockToken);

    // Re-inject service to trigger constructor
    const newService = TestBed.inject(UserService);
    expect(newService.currentUser()).toEqual(payload);
  });
});
