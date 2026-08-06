import { TestBed } from '@angular/core/testing';
import { CanActivateFn, provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { adminGlobalGuard } from './admin-global-guard';
import { Auth } from '../services/auth';

describe('adminGlobalGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => adminGlobalGuard(...guardParameters));

  let auth: Auth;
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    });
    auth = TestBed.inject(Auth);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate');
  });

  afterEach(() => localStorage.clear());

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('should allow access for ADMIN_GLOBAL', () => {
    (auth as any).authState.set({
      id: null, token: 'fake', role: 'ADMIN_GLOBAL', matricule: null,
      type: null, nom: 'Admin', prenom: 'Test', siteId: null
    });

    const result = executeGuard({} as any, {} as any);
    expect(result).toBe(true);
  });

  it('should redirect ADMIN_SITE to dashboard', () => {
    (auth as any).authState.set({
      id: null, token: 'fake', role: 'ADMIN_SITE', matricule: null,
      type: null, nom: 'Admin', prenom: 'Test', siteId: 1
    });

    const result = executeGuard({} as any, {} as any);
    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/admin/dashboard']);
  });

  it('should redirect to login when not logged in', () => {
    const result = executeGuard({} as any, {} as any);
    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
