import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Home } from './home';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductService, Product } from '../../services/product.service';
import { CartService, Cart } from '../../services/cart.service';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;
  let productService: ProductService;
  let cartService: CartService;

  const mockProducts: Product[] = [
    { id: '1', name: 'Laptop', description: 'A powerful laptop', category: 'Electronics', price: 1000, quantity: 5, userId: 's1', sellerName: 'Seller 1' },
    { id: '2', name: 'Shoes', description: 'Running shoes', category: 'Fashion', price: 50, quantity: 10, userId: 's2', sellerName: 'Seller 2' },
    { id: '3', name: 'Headphones', description: 'Noise cancelling', category: 'Electronics', price: 200, quantity: 0, userId: 's1', sellerName: 'Seller 1' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home, FormsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        ProductService,
        CartService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    productService = TestBed.inject(ProductService);
    cartService = TestBed.inject(CartService);
    
    spyOn(productService, 'getAllProducts').and.returnValue(of(mockProducts));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products on init', () => {
    expect(component.products()).toEqual(mockProducts);
  });

  it('should filter products by search term', () => {
    component.searchTerm.set('Laptop');
    expect(component.filteredProducts().length).toBe(1);
    expect(component.filteredProducts()[0].name).toBe('Laptop');
  });

  it('should filter products by category', () => {
    component.selectedCategory.set('Fashion');
    expect(component.filteredProducts().length).toBe(1);
    expect(component.filteredProducts()[0].category).toBe('Fashion');
  });

  it('should filter products by price range', () => {
    component.minPrice.set(100);
    component.maxPrice.set(300);
    expect(component.filteredProducts().length).toBe(1);
    expect(component.filteredProducts()[0].name).toBe('Headphones');
  });

  it('should extract unique categories', () => {
    expect(component.categories()).toEqual(['Electronics', 'Fashion']);
  });

  it('should add product to cart', () => {
    const mockCart: Cart = { id: 'c1', userId: 'u1', items: [] };
    const cartSpy = spyOn(cartService, 'addToCart').and.returnValue(of(mockCart));
    spyOn(window, 'alert');
    
    component.addToCart('1');
    
    expect(cartSpy).toHaveBeenCalledWith('1', 1);
    expect(window.alert).toHaveBeenCalledWith('Produit ajouté au panier !');
  });

  it('should not add to cart if out of stock', () => {
    const cartSpy = spyOn(cartService, 'addToCart');
    spyOn(window, 'alert');
    
    component.addToCart('3');
    
    expect(cartSpy).not.toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Désolé, ce produit est épuisé.');
  });

  it('should handle error when loading products', () => {
    const consoleSpy = spyOn(console, 'error');
    (productService.getAllProducts as jasmine.Spy).and.returnValue(throwError(() => new Error('Error')));
    
    component.ngOnInit();
    
    expect(consoleSpy).toHaveBeenCalled();
  });
});
