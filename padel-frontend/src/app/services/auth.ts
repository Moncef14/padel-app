import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import {
  LoginRequest, LoginResponse,
  MembreLoginRequest, MembreLoginResponse,
  RegisterRequest, RegisterResponse,
  AuthState
} from '../models/auth.model';

const API_URL = 'http://localhost:8080/api/auth';
const STORAGE_KEY = 'padel_auth';

@Injectable({
  providedIn: 'root'
})
export class Auth {

  // Signal qui contient l'état d'authentification courant, ou null si déconnecté.
  // On utilise un signal (plutôt qu'un simple champ) pour que les composants
  // et guards réagissent automatiquement aux changements de connexion.
  private authState = signal<AuthState | null>(this.loadFromStorage());

  // Signaux dérivés pratiques à consommer dans les guards/components
  readonly currentUser = computed(() => this.authState());
  readonly isLoggedIn = computed(() => this.authState() !== null);
  readonly isAdmin = computed(() => this.authState()?.role !== null && this.authState()?.role !== undefined);
  readonly isMembre = computed(() => this.authState()?.matricule !== null && this.authState()?.matricule !== undefined);
  readonly isAdminGlobal = computed(() => this.authState()?.role === 'ADMIN_GLOBAL');

  constructor(private http: HttpClient) {}

  // Récupère l'état sauvegardé au démarrage de l'app pour rester connecté
  // après un rafraîchissement de page (le token JWT vit 24h côté backend).
  private loadFromStorage(): AuthState | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as AuthState;
    } catch {
      return null;
    }
  }

  private saveToStorage(state: AuthState): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    this.authState.set(state);
  }

  // Connexion administrateur (email + mot de passe)
  loginAdmin(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/login`, request).pipe(
      tap(response => {
        this.saveToStorage({
          id: null, // un admin n'a pas d'id de membre
          token: response.token,
          role: response.role,
          matricule: null,
          type: null,
          nom: response.nom,
          prenom: '',
          siteId: null // le siteId est dans le JWT, pas dans la réponse — le backend le lit du token
        });
      })
    );
  }

  // Connexion membre (matricule + mot de passe optionnel)
  loginMembre(request: MembreLoginRequest): Observable<MembreLoginResponse> {
    return this.http.post<MembreLoginResponse>(`${API_URL}/membre`, request).pipe(
      tap(response => {
        this.saveToStorage({
          id: response.id,
          token: response.token,
          role: null,
          matricule: response.matricule,
          type: response.type,
          nom: response.nom,
          prenom: response.prenom,
          siteId: response.siteId
        });
      })
    );
  }

  // Inscription d'un nouveau membre (auto-service, génère le matricule côté backend)
  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${API_URL}/register`, request).pipe(
      tap(response => {
        this.saveToStorage({
          id: response.id,
          token: response.token,
          role: null,
          matricule: response.matricule,
          type: response.type,
          nom: response.nom,
          prenom: response.prenom,
          siteId: response.siteId
        });
      })
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.authState.set(null);
  }

  getToken(): string | null {
    return this.authState()?.token ?? null;
  }

  getMembreId(): number | null {
    return this.authState()?.id ?? null;
  }

  getMembreType(): string | null {
    return this.authState()?.type ?? null;
  }

  getMembreSiteId(): number | null {
    return this.authState()?.siteId ?? null;
  }
}