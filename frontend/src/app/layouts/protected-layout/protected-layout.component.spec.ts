/// <reference types="jasmine" />

import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProtectedLayoutComponent } from './protected-layout.component';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';

const authSessionServiceMock: Pick<AuthSessionService, 'logout' | 'navigateToLogin'> = {
  logout: () => of(null),
  navigateToLogin: () => undefined,
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
