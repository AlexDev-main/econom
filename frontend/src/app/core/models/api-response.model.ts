export interface ApiResponse<T> {
  success: boolean;
  status: number;
  code?: string;
  message?: string;
  data?: T;
  timestamp: string;
}
