import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, timeout } from 'rxjs';

import { API_CONFIG } from 'src/app/core/config/api.config';
import { API_ENDPOINTS } from 'src/app/core/constants/api-endpoints';
import { ApiResponse } from 'src/app/core/models/api-response.model';
import {
  LoginRequest,
  RefreshTokenRequest,
  TokenResponse,
} from '../models';
import { TokenStorageService } from 'src/app/features/auth/services/token-storage.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly baseUrl = API_CONFIG.baseUrl;
  private readonly requestTimeoutMs = API_CONFIG.timeoutMs;

  constructor(
    private readonly http: HttpClient,
    private readonly tokenStorageService: TokenStorageService,
  ) {}

  login(payload: LoginRequest): Observable<ApiResponse<TokenResponse>> {
    return this.http
      .post<ApiResponse<TokenResponse>>(
        this.buildUrl(API_ENDPOINTS.auth.login),
        payload,
      )
      .pipe(
        timeout(this.requestTimeoutMs),
        tap((response) => this.persistSessionIfPresent(response)),
      );
  }

  refreshToken(
    payload: RefreshTokenRequest,
  ): Observable<ApiResponse<TokenResponse>> {
    return this.http
      .post<ApiResponse<TokenResponse>>(
        this.buildUrl(API_ENDPOINTS.auth.refresh),
        payload,
      )
      .pipe(
        timeout(this.requestTimeoutMs),
        tap((response) => this.persistSessionIfPresent(response)),
      );
  }

  logout(payload: RefreshTokenRequest): Observable<ApiResponse<void>> {
    return this.http
      .post<ApiResponse<void>>(this.buildUrl(API_ENDPOINTS.auth.logout), payload)
      .pipe(
        timeout(this.requestTimeoutMs),
        tap(() => this.tokenStorageService.clearSession()),
      );
  }

  ssoCallback(code: string, state: string): Observable<ApiResponse<TokenResponse>> {
    return this.http
      .get<ApiResponse<TokenResponse>>(this.buildUrl(API_ENDPOINTS.auth.ssoCallback), {
        params: {
          code,
          state,
        },
      })
      .pipe(
        timeout(this.requestTimeoutMs),
        tap((response) => this.persistSessionIfPresent(response)),
      );
  }

  ssoLogin(): Observable<ApiResponse<TokenResponse>> {
    return this.http
      .get<ApiResponse<TokenResponse>>(this.buildUrl(API_ENDPOINTS.auth.sso))
      .pipe(
        timeout(this.requestTimeoutMs),
        tap((response) => this.persistSessionIfPresent(response)),
      );
  }

  getSsoUrl(): string {
    return this.buildUrl(API_ENDPOINTS.auth.sso);
  }

  private buildUrl(endpoint: string): string {
    return `${this.baseUrl}${endpoint}`;
  }

  private persistSessionIfPresent(response: ApiResponse<TokenResponse>): void {
    const tokenResponse = response.data;

    if (!tokenResponse) {
      return;
    }

    this.tokenStorageService.setAccessToken(tokenResponse.accessToken);
    this.tokenStorageService.setRefreshToken(tokenResponse.refreshToken);
  }
}
