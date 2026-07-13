import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthErrorService {
  resolveMessage(error: unknown): string {
    const defaultMessage = 'No fue posible completar la autenticación. Inténtalo de nuevo.';

    if (error instanceof HttpErrorResponse) {
      const apiError = error.error as Partial<ApiResponse<unknown>> | null;

      if (apiError?.message) {
        return apiError.message;
      }

      if (error.status === 0) {
        return 'No hay conexión con el servidor.';
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
