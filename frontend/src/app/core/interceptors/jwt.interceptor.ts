import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, switchMap, throwError } from 'rxjs';

import { API_CONFIG } from '../config/api.config';
import { PUBLIC_API_ENDPOINTS } from '../constants/api-endpoints';
import { AuthErrorService } from '../services/auth-error.service';
import { AuthSessionService } from '../services/auth-session.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(
    private readonly authSessionService: AuthSessionService,
    private readonly authErrorService: AuthErrorService,
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    const shouldAttachToken = this.shouldAttachToken(request.url);
    const token = shouldAttachToken ? this.authSessionService.getAccessToken() : null;
    const requestWithToken = token ? this.addToken(request, token) : request;

    return next.handle(requestWithToken).pipe(
      catchError((error: unknown) => {
        if (!this.shouldTryRefresh(error, request.url)) {
          return throwError(() => error);
        }

        return this.authSessionService.refreshAccessToken().pipe(
          switchMap((newToken) => next.handle(this.addToken(request, newToken))),
          catchError((refreshError: unknown) => {
            this.authSessionService.clearSession();
            return throwError(() => refreshError);
          }),
        );
      }),
    );
  }

  private shouldAttachToken(url: string): boolean {
    if (!url.startsWith(API_CONFIG.baseUrl)) {
      return false;
    }

    return !this.isPublicEndpoint(url);
  }

  private shouldTryRefresh(error: unknown, url: string): boolean {
    if (!this.authErrorService.isUnauthorized(error)) {
      return false;
    }

    if (url.includes('/auth/logout')) {
      return false;
    }

    if (!url.startsWith(API_CONFIG.baseUrl)) {
      return false;
    }

    if (this.isPublicEndpoint(url)) {
      return false;
    }

    if (!this.authSessionService.hasValidSession()) {
      return false;
    }

    return true;
  }

  private isPublicEndpoint(url: string): boolean {
    return PUBLIC_API_ENDPOINTS.some((endpoint) => url.includes(endpoint));
  }

  private addToken(request: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }
}
