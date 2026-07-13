export const API_ENDPOINTS = {
  auth: {
    login: '/auth/login',
    refreshToken: '/auth/refresh-token',
    logout: '/auth/logout',
    profile: '/auth/profile',
  },
} as const;
