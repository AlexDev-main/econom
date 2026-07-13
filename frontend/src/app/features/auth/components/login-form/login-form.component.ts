import { Component } from '@angular/core';
import {
  FormBuilder,
  Validators
} from '@angular/forms';
import { EMPTY, finalize, tap, catchError } from 'rxjs';

import { ApiResponse } from 'src/app/core/models/api-response.model';
import { AuthErrorService } from 'src/app/core/services/auth-error.service';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';
import { LoginRequest, TokenResponse } from '../../models';

type LoginState = 'idle' | 'loading' | 'success' | 'error';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {

  hidePassword = true;
  authState: LoginState = 'idle';
  authErrorMessage: string | null = null;

  form = this.fb.nonNullable.group({
    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(6)
      ]
    ]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly authSessionService: AuthSessionService,
    private readonly authErrorService: AuthErrorService,
  ) {}

  get isLoading(): boolean {
    return this.authState === 'loading';
  }

  login(): void {

    if (this.isLoading) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.authState = 'loading';
    this.authErrorMessage = null;
    this.form.disable({ emitEvent: false });

    const payload: LoginRequest = this.form.getRawValue();

    this.authSessionService
      .login(payload)
      .pipe(
        tap((response) => this.ensureTokenResponse(response)),
        tap(() => {
          this.authState = 'success';
          this.authSessionService.navigateToProtectedArea();
        }),
        catchError((error: unknown) => {
          this.authState = 'error';
          this.authErrorMessage = this.authErrorService.resolveMessage(error);
          return EMPTY;
        }),
        finalize(() => {
          this.form.enable({ emitEvent: false });

          if (this.authState === 'loading') {
            this.authState = 'idle';
          }
        }),
      )
      .subscribe();

  }

  loginSSO(): void {
    if (this.isLoading) {
      return;
    }

    this.authState = 'loading';
    this.authErrorMessage = null;
    this.form.disable({ emitEvent: false });

    this.authSessionService
      .startSsoLogin()
      .pipe(
        tap((response) => this.ensureTokenResponse(response)),
        tap(() => {
          this.authState = 'success';
          this.authSessionService.navigateToProtectedArea();
        }),
        catchError((error: unknown) => {
          this.authState = 'error';
          this.authErrorMessage = this.authErrorService.resolveMessage(error);
          return EMPTY;
        }),
        finalize(() => {
          this.form.enable({ emitEvent: false });

          if (this.authState === 'loading') {
            this.authState = 'idle';
          }
        }),
      )
      .subscribe();
  }

  private ensureTokenResponse(response: ApiResponse<TokenResponse>): void {
    if (response.success && response.data) {
      return;
    }

    throw new Error(response.message ?? 'No fue posible iniciar sesión.');
  }

}