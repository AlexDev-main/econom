import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { AuthRoutingModule } from './auth-routing.module';
import { LoginComponent } from './pages/login/login.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { ComponentsModule } from 'src/app/components/components.module';
import { SsoCallbackComponent } from './pages/sso-callback/sso-callback.component';
import { ProtectedLayoutComponent } from 'src/app/layouts/protected-layout/protected-layout.component';

@NgModule({
  declarations: [
    LoginComponent,
    AuthLayoutComponent,
    LoginFormComponent,
    SsoCallbackComponent,
    ProtectedLayoutComponent,
  ],
  imports: [
    SharedModule,
    AuthRoutingModule,
    ComponentsModule
  ]
})
export class AuthModule { }
