import { Component } from '@angular/core';
import { I18nService } from 'src/app/core/services/i18n.service';

@Component({
  selector: 'app-auth-layout',
  templateUrl: './auth-layout.component.html',
  styleUrls: ['./auth-layout.component.scss']
})
export class AuthLayoutComponent {
  constructor(private readonly i18nService: I18nService) {}

  translate(key: string): string {
    return this.i18nService.translate(key);
  }
}
