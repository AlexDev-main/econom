/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ProtectedLayoutComponent } from './protected-layout.component';

describe('ProtectedLayoutComponent', () => {
  let component: ProtectedLayoutComponent;
  let fixture: ComponentFixture<ProtectedLayoutComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProtectedLayoutComponent],
      imports: [RouterTestingModule],
    });

    fixture = TestBed.createComponent(ProtectedLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
