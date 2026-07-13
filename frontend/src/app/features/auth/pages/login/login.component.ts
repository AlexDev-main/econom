import { Component } from '@angular/core';
import { I18nService } from 'src/app/core/services/i18n.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  constructor(private readonly i18nService: I18nService) {}

  translate(key: string): string {
    return this.i18nService.translate(key);
  }
}
