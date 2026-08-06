import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { MesMatchs } from './mes-matchs';
import { Auth } from '../../../services/auth';

describe('MesMatchs', () => {
  let component: MesMatchs;
  let fixture: ComponentFixture<MesMatchs>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      imports: [MesMatchs],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    const auth = TestBed.inject(Auth);
    (auth as any).authState.set({
      id: 1, token: 'fake', role: null, matricule: 'M001',
      type: 'LIBRE', nom: 'Test', prenom: 'User', siteId: null
    });

    fixture = TestBed.createComponent(MesMatchs);
    component = fixture.componentInstance;
    fixture.detectChanges();

    httpMock = TestBed.inject(HttpTestingController);
    httpMock.expectOne('http://localhost:8080/api/matchs/membre/1').flush([]);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});