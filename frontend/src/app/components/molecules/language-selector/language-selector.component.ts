import { Component, OnDestroy } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';

import { AppLanguage, I18nService } from 'src/app/core/services/i18n.service';

@Component({
  selector: 'app-language-selector',
  templateUrl: './language-selector.component.html',
  styleUrls: ['./language-selector.component.scss'],
})
export class LanguageSelectorComponent implements OnDestroy {
  readonly languages: readonly AppLanguage[] = this.i18nService.availableLanguages;
  selectedLanguage: AppLanguage = 'es';

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly i18nService: I18nService) {
    this.i18nService.currentLanguage$
      .pipe(takeUntil(this.destroy$))
      .subscribe((language) => {
        this.selectedLanguage = language;
      });
  }

  get orderedLanguages(): AppLanguage[] {
    const uniqueLanguages = Array.from(new Set(this.languages));

    if (!uniqueLanguages.includes(this.selectedLanguage)) {
      return ['es', ...uniqueLanguages.filter((language) => language !== 'es')];
    }

    return [
      this.selectedLanguage,
      ...uniqueLanguages.filter((language) => language !== this.selectedLanguage),
    ];
  }

  onLanguageChange(language: string): void {
    const normalizedLanguage = this.resolveSupportedLanguage(language);

    if (!normalizedLanguage) {
      return;
    }

    this.selectedLanguage = normalizedLanguage;
    this.i18nService.setLanguage(normalizedLanguage);
  }

  translate(key: string): string {
    return this.i18nService.translate(key);
  }

  trackByLanguage(_: number, language: AppLanguage): AppLanguage {
    return language;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private resolveSupportedLanguage(language: string): AppLanguage | null {
    const availableLanguages = new Set(this.i18nService.availableLanguages);

    if (!availableLanguages.has(language as AppLanguage)) {
      return null;
    }

    return language as AppLanguage;
  }
}