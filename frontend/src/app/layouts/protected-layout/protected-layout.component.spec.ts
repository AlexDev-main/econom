/// <reference types="jasmine" />

import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProtectedLayoutComponent } from './protected-layout.component';
import { I18nService } from 'src/app/core/services/i18n.service';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';

const authSessionServiceMock: Pick<AuthSessionService, 'logout' | 'navigateToLogin'> = {
  logout: () => of(null),
  navigateToLogin: () => undefined,
};

const i18nServiceMock: Pick<I18nService, 'translate'> = {
  translate: (key: string) => key,
};

describe('ProtectedLayoutComponent', () => {
  let component: ProtectedLayoutComponent;
  let fixture: ComponentFixture<ProtectedLayoutComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProtectedLayoutComponent],
      imports: [RouterTestingModule],
      providers: [
        { provide: AuthSessionService, useValue: authSessionServiceMock },
        { provide: I18nService, useValue: i18nServiceMock },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    });

    fixture = TestBed.createComponent(ProtectedLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
