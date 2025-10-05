export interface ApiError {
  message: string;
  code?: string;
  status?: number;
  details?: Record<string, string[]>;
}

export class ApiErrorHandler {
  static handleError(error: unknown): ApiError {
    console.error('API Error:', error);

    // Handle Axios errors
    if (error && typeof error === 'object' && 'response' in error) {
      const axiosError = error as { response?: { status: number; data?: unknown } };
      const response = axiosError.response;

      if (response) {
        return {
          message: this.getErrorMessage(response.status, response.data),
          code: this.getErrorCodeFromData(response.data) || this.getErrorCode(response.status),
          status: response.status,
          details: this.getErrorDetails(response.data),
        };
      }
    }

    // Handle network errors
    if (error instanceof TypeError && error.message.includes('fetch')) {
      return {
        message: 'Network error. Please check your internet connection.',
        code: 'NETWORK_ERROR',
        status: 0,
      };
    }

    // Handle generic errors
    if (error instanceof Error) {
      return {
        message: error.message,
        code: 'GENERIC_ERROR',
      };
    }

    // Fallback for unknown errors
    return {
      message: 'An unexpected error occurred. Please try again.',
      code: 'UNKNOWN_ERROR',
    };
  }

  private static getErrorMessage(status: number, data?: unknown): string {
    // Use server-provided message if available
    if (data?.message) {
      return data.message;
    }

    // Use status-based messages
    switch (status) {
      case 400:
        return 'Invalid request. Please check your input.';
      case 401:
        return 'You are not authorized to perform this action.';
      case 403:
        return 'You do not have permission to access this resource.';
      case 404:
        return 'The requested resource was not found.';
      case 409:
        return 'This action conflicts with the current state.';
      case 422:
        return 'The provided data is invalid.';
      case 429:
        return 'Too many requests. Please try again later.';
      case 500:
        return 'Server error. Please try again later.';
      case 503:
        return 'Service temporarily unavailable. Please try again later.';
      default:
        return 'An error occurred. Please try again.';
    }
  }

  private static getErrorCode(status: number): string {
    switch (status) {
      case 400:
        return 'VALIDATION_ERROR';
      case 401:
        return 'UNAUTHORIZED';
      case 403:
        return 'FORBIDDEN';
      case 404:
        return 'NOT_FOUND';
      case 409:
        return 'CONFLICT';
      case 422:
        return 'UNPROCESSABLE_ENTITY';
      case 429:
        return 'RATE_LIMITED';
      case 500:
        return 'SERVER_ERROR';
      case 503:
        return 'SERVICE_UNAVAILABLE';
      default:
        return 'UNKNOWN_ERROR';
    }
  }

  private static getErrorCodeFromData(data?: unknown): string | undefined {
    if (data && typeof data === 'object' && data !== null && 'code' in data) {
      return (data as { code: string }).code;
    }
    return undefined;
  }

  private static getErrorDetails(data?: unknown): Record<string, string[]> | undefined {
    if (data && typeof data === 'object' && data !== null && 'details' in data) {
      return (data as { details: Record<string, string[]> }).details;
    }
    return undefined;
  }

  static isRetryableError(error: ApiError): boolean {
    const retryableCodes = ['NETWORK_ERROR', 'SERVER_ERROR', 'SERVICE_UNAVAILABLE', 'RATE_LIMITED'];
    return retryableCodes.includes(error.code || '');
  }

  static getRetryDelay(attempt: number): number {
    // Exponential backoff: 1s, 2s, 4s, 8s, max 10s
    return Math.min(1000 * Math.pow(2, attempt), 10000);
  }
}

// Utility function for handling async operations with retry logic
export async function withRetry<T>(
  operation: () => Promise<T>,
  maxRetries: number = 3,
  onRetry?: (attempt: number, error: ApiError) => void
): Promise<T> {
  let lastError: ApiError;

  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      return await operation();
    } catch (error) {
      lastError = ApiErrorHandler.handleError(error);

      // Don't retry if it's not a retryable error or if it's the last attempt
      if (!ApiErrorHandler.isRetryableError(lastError) || attempt === maxRetries) {
        throw lastError;
      }

      // Call onRetry callback if provided
      if (onRetry) {
        onRetry(attempt + 1, lastError);
      }

      // Wait before retrying
      const delay = ApiErrorHandler.getRetryDelay(attempt);
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw lastError!;
}

// Toast notification helper for errors
export function showErrorToast(error: ApiError, toast: (options: { title: string; description: string; variant: string }) => void) {
  toast({
    title: 'Error',
    description: error.message,
    variant: 'destructive',
  });
}

// Toast notification helper for success
export function showSuccessToast(message: string, toast: (options: { title: string; description: string; variant: string }) => void) {
  toast({
    title: 'Success',
    description: message,
    variant: 'default',
  });
}