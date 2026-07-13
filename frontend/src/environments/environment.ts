import { EnvironmentConfig } from './environment.interface';

export const environment: EnvironmentConfig = {
  production: false,
  apiUrl: 'http://localhost:3000/api',
  requestTimeoutMs: 15000,
};
