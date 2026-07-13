import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SsoCallbackComponent } from './sso-callback.component';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';
import { AuthErrorService } from 'src/app/core/services/auth-error.service';

describe('SsoCallbackComponent', () => {
  let component: SsoCallbackComponent;
  let fixture: ComponentFixture<SsoCallbackComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SsoCallbackComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: (key: string) => (key === 'code' ? 'code-value' : 'state-value'),
              },
            },
          },
        },
        {
          provide: AuthSessionService,
          useValue: {
            handleSsoCallback: () => of(null),
            navigateToLogin: () => undefined,
            navigateToProtectedArea: () => undefined,
          },
        },
        {
          provide: AuthErrorService,
          useValue: {
            resolveMessage: () => 'Error',
          },
        },
      ],
    });

    fixture = TestBed.createComponent(SsoCallbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
