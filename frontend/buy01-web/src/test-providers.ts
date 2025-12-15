import { Provider } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
const testProviders: Provider[] = [
  provideHttpClientTesting(),
];
export default testProviders;
