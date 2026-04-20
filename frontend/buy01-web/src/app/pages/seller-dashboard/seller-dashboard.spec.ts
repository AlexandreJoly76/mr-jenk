import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellerDashboard } from './seller-dashboard';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductService, Product } from '../../services/product.service';
import { UserService } from '../../services/user.service';
import { of, throwError } from 'rxjs';

describe('SellerDashboard', () => {
  let component: SellerDashboard;
  let fixture: ComponentFixture<SellerDashboard>;
  let productService: ProductService;
  let userService: UserService;

  const mockUser = { id: 'seller123', name: 'John Seller' };
  const mockProducts: Product[] = [
    { id: '1', name: 'P1', description: 'D1', category: 'C1', price: 10, quantity: 1, userId: 'seller123', sellerName: 'John Seller' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerDashboard],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        ProductService,
        UserService
      ]
    }).compileComponents();

    productService = TestBed.inject(ProductService);
    userService = TestBed.inject(UserService);
    
    spyOn(userService, 'getUserInfoFromToken').and.returnValue(mockUser);
    spyOn(productService, 'getProductsBySeller').and.returnValue(of(mockProducts));

    fixture = TestBed.createComponent(SellerDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load seller products on init', () => {
    expect(userService.getUserInfoFromToken).toHaveBeenCalled();
    expect(productService.getProductsBySeller).toHaveBeenCalledWith('seller123');
    expect(component.myProducts()).toEqual(mockProducts);
  });

  it('should delete product when confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    const deleteSpy = spyOn(productService, 'deleteProduct').and.returnValue(of(undefined));
    const loadSpy = spyOn(component, 'loadMyProducts').and.callThrough();
    
    component.delete('1');
    
    expect(deleteSpy).toHaveBeenCalledWith('1');
    expect(loadSpy).toHaveBeenCalled();
  });

  it('should not delete product when not confirmed', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    const deleteSpy = spyOn(productService, 'deleteProduct');
    
    component.delete('1');
    
    expect(deleteSpy).not.toHaveBeenCalled();
  });

  it('should handle error on delete', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    spyOn(window, 'alert');
    spyOn(productService, 'deleteProduct').and.returnValue(throwError(() => new Error('Failed')));
    
    component.delete('1');
    
    expect(window.alert).toHaveBeenCalledWith("Erreur lors de la suppression");
  });

  it('should handle error when loading products', () => {
    const consoleSpy = spyOn(console, 'error');
    (productService.getProductsBySeller as jasmine.Spy).and.returnValue(throwError(() => new Error('Error')));
    
    component.loadMyProducts();
    
    expect(consoleSpy).toHaveBeenCalled();
  });
});
