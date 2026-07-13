import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, catchError, finalize, map, of, shareReplay, tap, throwError } from 'rxjs';

import { APP_ROUTES } from '../constants/app-routes';
import { ApiResponse } from '../models/api-response.model';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { TokenStorageService } from 'src/app/features/auth/services/token-storage.service';
import { LoginRequest, RefreshTokenRequest, TokenResponse } from 'src/app/features/auth/models';

@Injectable({
  providedIn: 'root',
})
export class AuthSessionService {
  private readonly isAuthenticatedSubject = new BehaviorSubject<boolean>(
    this.tokenStorageService.hasSession(),
  );

  private refreshRequest$: Observable<TokenResponse> | null = null;

  readonly isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(
    private readonly authService: AuthService,
    private readonly tokenStorageService: TokenStorageService,
    private readonly router: Router,
  ) {}

  login(payload: LoginRequest): Observable<ApiResponse<TokenResponse>> {
    return this.authService.login(payload).pipe(
      tap((response) => this.assertTokenResponse(response)),
      tap(() => this.setAuthenticatedState(true)),
    );
  }

  handleSsoCallback(code: string, state: string): Observable<ApiResponse<TokenResponse>> {
    return this.authService.ssoCallback(code, state).pipe(
      tap((response) => this.assertTokenResponse(response)),
      tap(() => this.setAuthenticatedState(true)),
    );
  }

  startSsoLogin(): Observable<ApiResponse<TokenResponse>> {
    return this.authService.ssoLogin().pipe(
      tap((response) => this.assertTokenResponse(response)),
      tap(() => this.setAuthenticatedState(true)),
    );
  }

  logout(): Observable<ApiResponse<void> | null> {
    const refreshToken = this.tokenStorageService.getRefreshToken();

    if (!refreshToken) {
      this.clearSession();
      return of(null);
    }

    const payload: RefreshTokenRequest = { refreshToken };

    return this.authService.logout(payload).pipe(
      catchError(() => of(null)),
      finalize(() => this.clearSession()),
    );
  }

  refreshAccessToken(): Observable<string> {
    if (!this.refreshRequest$) {
      this.refreshRequest$ = this.executeRefresh().pipe(
        shareReplay(1),
        finalize(() => {
          this.refreshRequest$ = null;
        }),
      );
    }

    return this.refreshRequest$.pipe(map((tokenResponse) => tokenResponse.accessToken));
  }

  hasValidSession(): boolean {
    return this.tokenStorageService.hasSession();
  }

  getAccessToken(): string | null {
    return this.tokenStorageService.getAccessToken();
  }

  setAuthenticatedState(isAuthenticated: boolean): void {
    this.isAuthenticatedSubject.next(isAuthenticated);
  }

  navigateToProtectedArea(): void {
    void this.router.navigateByUrl(APP_ROUTES.protectedHome);
  }

  navigateToLogin(): void {
    void this.router.navigateByUrl(APP_ROUTES.authLogin);
  }

  clearSession(): void {
    this.tokenStorageService.clearSession();
    this.setAuthenticatedState(false);
  }

  private executeRefresh(): Observable<TokenResponse> {
    const refreshToken = this.tokenStorageService.getRefreshToken();

    if (!refreshToken) {
      this.clearSession();
      return throwError(() => new Error('No hay sesión activa.'));
    }

    const payload: RefreshTokenRequest = { refreshToken };

    return this.authService.refreshToken(payload).pipe(
      map((response) => {
        this.assertTokenResponse(response);

        const tokenResponse = response.data;

        if (!tokenResponse) {
          throw new Error('Respuesta de refresh inválida.');
        }

        return tokenResponse;
      }),
      tap(() => this.setAuthenticatedState(true)),
      catchError((error: unknown) => {
        this.clearSession();
        return throwError(() => error);
      }),
    );
  }

  private assertTokenResponse(response: ApiResponse<TokenResponse>): void {
    if (response.success && response.data) {
      return;
    }

    throw new Error(response.message ?? 'Respuesta de autenticación inválida.');
  }
}
