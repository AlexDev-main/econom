import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EMPTY, Subject, catchError, finalize, takeUntil } from 'rxjs';

import { AuthErrorService } from 'src/app/core/services/auth-error.service';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';

type SsoState = 'loading' | 'success' | 'error';

@Component({
  selector: 'app-sso-callback',
  template: '',
})
export class SsoCallbackComponent implements OnInit, OnDestroy {
  state: SsoState = 'loading';
  errorMessage: string | null = null;

  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly authSessionService: AuthSessionService,
    private readonly authErrorService: AuthErrorService,
  ) {}

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    const state = this.route.snapshot.queryParamMap.get('state');

    if (!code || !state) {
      this.state = 'error';
      this.errorMessage = 'Respuesta SSO inválida.';
      this.authSessionService.navigateToLogin();
      return;
    }

    this.authSessionService
      .handleSsoCallback(code, state)
      .pipe(
        takeUntil(this.destroy$),
        catchError((error: unknown) => {
          this.state = 'error';
          this.errorMessage = this.authErrorService.resolveMessage(error);
          this.authSessionService.navigateToLogin();
          return EMPTY;
        }),
        finalize(() => {
          if (this.state !== 'error') {
            this.state = 'success';
            this.authSessionService.navigateToProtectedArea();
          }
        }),
      )
      .subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
