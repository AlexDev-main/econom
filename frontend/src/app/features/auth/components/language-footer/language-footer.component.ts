import { Component } from '@angular/core';

import { AppLanguage, I18nService } from 'src/app/core/services/i18n.service';

@Component({
  selector: 'app-language-footer',
  templateUrl: './language-footer.component.html',
  styleUrls: ['./language-footer.component.scss'],
})
export class LanguageFooterComponent {
  readonly languages = this.i18nService.availableLanguages;
  readonly currentLanguage$ = this.i18nService.currentLanguage$;

  constructor(private readonly i18nService: I18nService) {}

  onLanguageChange(language: string): void {
    this.i18nService.setLanguage(language);
  }

  translate(key: string): string {
    return this.i18nService.translate(key);
  }

  trackByLanguage(_: number, language: AppLanguage): AppLanguage {
    return language;
  }
}