import { environment } from '../../../environments/environment';

export const API_CONFIG = {
  baseUrl: environment.apiUrl,
  timeoutMs: environment.requestTimeoutMs,
} as const;
