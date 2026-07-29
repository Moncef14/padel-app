import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { adminGlobalGuard } from './admin-global-guard';

describe('adminGlobalGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => adminGlobalGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
