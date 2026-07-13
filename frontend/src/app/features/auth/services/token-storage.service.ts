import { Injectable } from '@angular/core';

import { STORAGE_KEYS } from 'src/app/core/constants/storage-keys';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  setAccessToken(token: string): void {
    this.setItem(STORAGE_KEYS.accessToken, token);
  }

  setRefreshToken(token: string): void {
    this.setItem(STORAGE_KEYS.refreshToken, token);
  }

  getAccessToken(): string | null {
    return this.getItem(STORAGE_KEYS.accessToken);
  }

  getRefreshToken(): string | null {
    return this.getItem(STORAGE_KEYS.refreshToken);
  }

  hasSession(): boolean {
    return !!this.getAccessToken() && !!this.getRefreshToken();
  }

  removeAccessToken(): void {
    this.removeItem(STORAGE_KEYS.accessToken);
  }

  removeRefreshToken(): void {
    this.removeItem(STORAGE_KEYS.refreshToken);
  }

  clearSession(): void {
    this.removeAccessToken();
    this.removeRefreshToken();
  }

  private setItem(key: string, value: string): void {
    localStorage.setItem(key, value);
  }

  private getItem(key: string): string | null {
    return localStorage.getItem(key);
  }

  private removeItem(key: string): void {
    localStorage.removeItem(key);
  }
}
