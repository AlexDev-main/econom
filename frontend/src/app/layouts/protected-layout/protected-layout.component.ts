import { Component } from '@angular/core';
import { finalize } from 'rxjs';

import { I18nService } from 'src/app/core/services/i18n.service';
import { AuthSessionService } from 'src/app/core/services/auth-session.service';

@Component({
  selector: 'app-protected-layout',
  templateUrl: './protected-layout.component.html',
  styleUrls: ['./protected-layout.component.scss'],
})
export class ProtectedLayoutComponent {
  isLoggingOut = false;

  constructor(
    private readonly authSessionService: AuthSessionService,
    private readonly i18nService: I18nService,
  ) {}

  translate(key: string): string {
    return this.i18nService.translate(key);
  }

  logout(): void {
    if (this.isLoggingOut) {
      return;
    }

    this.isLoggingOut = true;

    this.authSessionService
      .logout()
      .pipe(
        finalize(() => {
          this.isLoggingOut = false;
          this.authSessionService.navigateToLogin();
        }),
      )
      .subscribe();
  }
}
