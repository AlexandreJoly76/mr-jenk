import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SellerDashboard } from './seller-dashboard';
import {provideRouter} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('SellerDashboard', () => {
  let component: SellerDashboard;
  let fixture: ComponentFixture<SellerDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SellerDashboard],
      providers:[provideRouter([]),provideHttpClient(),provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SellerDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
