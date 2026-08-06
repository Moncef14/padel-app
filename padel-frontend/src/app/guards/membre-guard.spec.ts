import { TestBed } from '@angular/core/testing';
import { CanActivateFn, provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { membreGuard } from './membre-guard';
import { Auth } from '../services/auth';

describe('membreGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => membreGuard(...guardParameters));

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

  it('should allow access for a logged-in member', () => {
    (auth as any).authState.set({
      id: 1, token: 'fake', role: null, matricule: 'G0001',
      type: 'GLOBAL', nom: 'Test', prenom: 'User', siteId: null
    });

    const result = executeGuard({} as any, {} as any);
    expect(result).toBe(true);
  });

  it('should deny access for an admin', () => {
    (auth as any).authState.set({
      id: null, token: 'fake', role: 'ADMIN_GLOBAL', matricule: null,
      type: null, nom: 'Admin', prenom: 'Test', siteId: null
    });

    const result = executeGuard({} as any, {} as any);
    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should deny access when not logged in', () => {
    const result = executeGuard({} as any, {} as any);
    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
