import { HttpClient } from '@angular/common/http';
import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { BehaviorSubject, Observable, catchError, forkJoin, map, of, tap } from 'rxjs';

export type AppLanguage = 'en' | 'es' | 'fr' | 'pt';

const DEFAULT_LANGUAGE: AppLanguage = 'es';
const STORAGE_LANGUAGE_KEY = 'app_language';

@Injectable({
  providedIn: 'root',
})
export class I18nService {
  private readonly dictionaries: Partial<Record<AppLanguage, Record<string, string>>> = {};
  private readonly currentLanguageSubject = new BehaviorSubject<AppLanguage>(DEFAULT_LANGUAGE);

  readonly currentLanguage$ = this.currentLanguageSubject.asObservable();
  readonly availableLanguages: readonly AppLanguage[] = ['en', 'es', 'fr', 'pt'];

  constructor(
    private readonly http: HttpClient,
    private readonly title: Title,
    @Inject(DOCUMENT) private readonly document: Document,
  ) {}

  initialize(): void {
    const initialLanguage = this.readStoredLanguage() ?? DEFAULT_LANGUAGE;
    this.setLanguage(initialLanguage);
  }

  setLanguage(language: string): void {
    const nextLanguage = this.isSupportedLanguage(language) ? language : DEFAULT_LANGUAGE;

    forkJoin([this.loadDictionary('en'), this.loadDictionary(nextLanguage)]).subscribe(() => {
      this.currentLanguageSubject.next(nextLanguage);
      this.persistLanguage(nextLanguage);
      this.updateDocumentMeta(nextLanguage);
    });
  }

  getCurrentLanguage(): AppLanguage {
    return this.currentLanguageSubject.value;
  }

  translate(key: string): string {
    const currentLanguage = this.currentLanguageSubject.value;
    const currentDictionary = this.dictionaries[currentLanguage];
    const currentValue = currentDictionary?.[key];

    if (currentValue) {
      return currentValue;
    }

    if (currentLanguage !== 'en') {
      const fallbackValue = this.dictionaries.en?.[key];

      if (fallbackValue) {
        return fallbackValue;
      }
    }

    return key;
  }

  private loadDictionary(language: AppLanguage): Observable<void> {
    if (this.dictionaries[language]) {
      return of(void 0);
    }

    return this.http.get<Record<string, string>>(`assets/i18n/${language}.json`).pipe(
      tap((dictionary) => {
        this.dictionaries[language] = dictionary ?? {};
      }),
      map(() => void 0),
      catchError(() => {
        this.dictionaries[language] = {};
        return of(void 0);
      }),
    );
  }

  private isSupportedLanguage(language: string): language is AppLanguage {
    return this.availableLanguages.includes(language as AppLanguage);
  }

  private readStoredLanguage(): AppLanguage | null {
    try {
      const rawLanguage = localStorage.getItem(STORAGE_LANGUAGE_KEY);

      if (!rawLanguage || !this.isSupportedLanguage(rawLanguage)) {
        return null;
      }

      return rawLanguage;
    } catch {
      return null;
    }
  }

  private persistLanguage(language: AppLanguage): void {
    try {
      localStorage.setItem(STORAGE_LANGUAGE_KEY, language);
    } catch {
      // no-op when localStorage is not available
    }
  }

  private updateDocumentMeta(language: AppLanguage): void {
    this.document.documentElement.lang = language;
    this.title.setTitle(this.translate('app.meta.title'));
  }
}