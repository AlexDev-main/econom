/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthLayoutComponent } from './auth-layout.component';
import { I18nService } from 'src/app/core/services/i18n.service';

describe('AuthLayoutComponent', () => {
  let component: AuthLayoutComponent;
  let fixture: ComponentFixture<AuthLayoutComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AuthLayoutComponent],
      imports: [RouterTestingModule],
      providers: [
        {
          provide: I18nService,
          useValue: {
            translate: (key: string) => key,
          },
        },
      ],
    });
    fixture = TestBed.createComponent(AuthLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
