import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';

import { LoginFormComponent } from './login-form.component';
import { AuthErrorService } from 'src/app/core/services/auth-error.service';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';
import { I18nService } from 'src/app/core/services/i18n.service';

const authSessionServiceMock: Pick<
  AuthSessionService,
  'login' | 'navigateToProtectedArea' | 'startSsoLogin'
> = {
  login: () => of({
    success: true,
    status: 200,
    data: {
      accessToken: 'access-token',
      refreshToken: 'refresh-token',
      tokenType: 'Bearer',
      expiresIn: 900000,
    },
    timestamp: new Date().toISOString(),
  }),
  navigateToProtectedArea: () => undefined,
  startSsoLogin: () => of({
    success: true,
    status: 200,
    data: {
      accessToken: 'access-token',
      refreshToken: 'refresh-token',
      tokenType: 'Bearer',
      expiresIn: 900000,
    },
    timestamp: new Date().toISOString(),
  }),
};

const authErrorServiceMock: Pick<AuthErrorService, 'resolveMessage'> = {
  resolveMessage: () => 'Error',
};

const i18nServiceMock: Pick<I18nService, 'translate'> = {
  translate: (key: string) => key,
};

describe('LoginFormComponent', () => {
  let component: LoginFormComponent;
  let fixture: ComponentFixture<LoginFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LoginFormComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthSessionService, useValue: authSessionServiceMock },
        { provide: AuthErrorService, useValue: authErrorServiceMock },
        { provide: I18nService, useValue: i18nServiceMock },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    });
    fixture = TestBed.createComponent(LoginFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
