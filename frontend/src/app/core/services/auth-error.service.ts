import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { ApiResponse } from '../models/api-response.model';
import { I18nService } from './i18n.service';

@Injectable({
  providedIn: 'root',
})
export class AuthErrorService {
  constructor(private readonly i18nService: I18nService) {}

  resolveMessage(error: unknown): string {
    const defaultMessage = this.i18nService.translate('errors.auth.generic');

    if (error instanceof HttpErrorResponse) {
      const apiError = error.error as Partial<ApiResponse<unknown>> | null;

      if (apiError?.message) {
        return apiError.message;
      }

      if (error.status === 0) {
        return this.i18nService.translate('errors.auth.noServer');
      }
    }

    if (error instanceof Error && error.message) {
      return error.message;
    }

    return defaultMessage;
  }

  isUnauthorized(error: unknown): boolean {
    return error instanceof HttpErrorResponse && error.status === 401;
  }
}
