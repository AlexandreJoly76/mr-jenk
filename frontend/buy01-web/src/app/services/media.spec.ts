import { TestBed } from '@angular/core/testing';

import { Media } from './media';
import {provideRouter} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('Media', () => {
  let service: Media;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[provideRouter([]),provideHttpClient(),provideHttpClientTesting()]
    });
    service = TestBed.inject(Media);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
