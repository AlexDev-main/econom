export const API_ENDPOINTS = {
  auth: {
    login: '/auth/login',
    refresh: '/auth/refresh',
    logout: '/auth/logout',
    sso: '/auth/sso',
    ssoCallback: '/auth/sso/callback',
  },
} as const;

export const PUBLIC_API_ENDPOINTS: readonly string[] = [
  API_ENDPOINTS.auth.login,
  API_ENDPOINTS.auth.refresh,
  API_ENDPOINTS.auth.sso,
  API_ENDPOINTS.auth.ssoCallback,
] as const;
