import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { Auth } from './auth';

describe('Auth', () => {
  let service: Auth;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(Auth);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('isLoggedIn should be false when no user is stored', () => {
    expect(service.isLoggedIn()).toBe(false);
  });

  it('loginAdmin should store token and set isLoggedIn to true', () => {
    service.loginAdmin({ email: 'admin@test.be', password: 'test123' }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'fake-token', role: 'ADMIN_GLOBAL', nom: 'Test Admin' });

    expect(service.isLoggedIn()).toBe(true);
    expect(service.isAdmin()).toBe(true);
    expect(service.isAdminGlobal()).toBe(true);
  });

  it('loginMembre should store token and set isMembre to true', () => {
    service.loginMembre({ matricule: 'G0001' }).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/membre');
    req.flush({ id: 1, token: 'fake-token', matricule: 'G0001', type: 'GLOBAL', nom: 'Dubois', prenom: 'Marie', siteId: null });

    expect(service.isLoggedIn()).toBe(true);
    expect(service.isMembre()).toBe(true);
    expect(service.getMembreId()).toBe(1);
  });

  it('logout should clear the auth state', () => {
    service.loginMembre({ matricule: 'G0001' }).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/auth/membre');
    req.flush({ id: 1, token: 'fake-token', matricule: 'G0001', type: 'GLOBAL', nom: 'Dubois', prenom: 'Marie', siteId: null });

    service.logout();

    expect(service.isLoggedIn()).toBe(false);
    expect(service.getToken()).toBeNull();
  });

  it('getMembreId should return null for an admin', () => {
    service.loginAdmin({ email: 'admin@test.be', password: 'test123' }).subscribe();
    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    req.flush({ token: 'fake-token', role: 'ADMIN_GLOBAL', nom: 'Test Admin' });

    expect(service.getMembreId()).toBeNull();
  });
});
