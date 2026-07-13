import { Component } from '@angular/core';

import { I18nService } from 'src/app/core/services/i18n.service';

@Component({
  selector: 'app-language-footer',
  templateUrl: './language-footer.component.html',
  styleUrls: ['./language-footer.component.scss'],
})
export class LanguageFooterComponent {
  constructor(private readonly i18nService: I18nService) {}

  translate(key: string): string {
    return this.i18nService.translate(key);
  }
}