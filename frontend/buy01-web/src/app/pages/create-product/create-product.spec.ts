import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateProduct } from './create-product';
import {provideRouter} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('CreateProduct', () => {
  let component: CreateProduct;
  let fixture: ComponentFixture<CreateProduct>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateProduct],
      providers:[provideRouter([]),provideHttpClient(),provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateProduct);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
