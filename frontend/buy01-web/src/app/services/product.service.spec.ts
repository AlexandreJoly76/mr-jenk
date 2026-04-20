import { TestBed } from '@angular/core/testing';
import { ProductService, Product } from './product.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  const mockProducts: Product[] = [
    { id: '1', name: 'Product 1', description: 'Desc 1', category: 'Cat 1', price: 10, quantity: 5, userId: 'u1', sellerName: 'Seller 1' },
    { id: '2', name: 'Product 2', description: 'Desc 2', category: 'Cat 2', price: 20, quantity: 10, userId: 'u2', sellerName: 'Seller 2' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all products', () => {
    service.getAllProducts().subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne('https://localhost:8080/product-service/api/products');
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  it('should create a product', () => {
    const newProduct: Product = { name: 'New', description: 'Desc', category: 'Cat', price: 15, quantity: 3, userId: 'u1', sellerName: 'Seller 1' };
    const createdProduct = { ...newProduct, id: '3' };

    service.createProduct(newProduct).subscribe(product => {
      expect(product).toEqual(createdProduct);
    });

    const req = httpMock.expectOne('https://localhost:8080/product-service/api/products');
    expect(req.request.method).toBe('POST');
    req.flush(createdProduct);
  });

  it('should get products by seller', () => {
    const sellerId = 'u1';
    const sellerProducts = [mockProducts[0]];

    service.getProductsBySeller(sellerId).subscribe(products => {
      expect(products).toEqual(sellerProducts);
    });

    const req = httpMock.expectOne(`https://localhost:8080/product-service/api/products/seller/${sellerId}`);
    expect(req.request.method).toBe('GET');
    req.flush(sellerProducts);
  });

  it('should delete a product', () => {
    const productId = '1';

    service.deleteProduct(productId).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne(`https://localhost:8080/product-service/api/products/${productId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
