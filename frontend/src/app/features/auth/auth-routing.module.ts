import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { LoginComponent } from './pages/login/login.component';
import { SsoCallbackComponent } from './pages/sso-callback/sso-callback.component';
import { AuthGuard } from 'src/app/core/guards/auth.guard';
import { ProtectedLayoutComponent } from 'src/app/layouts/protected-layout/protected-layout.component';


const routes: Routes = [
  {
    path: 'sso/callback',
    component: SsoCallbackComponent,
  },
  {
    path: 'home',
    canActivate: [AuthGuard],
    component: ProtectedLayoutComponent,
  },
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full',
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
