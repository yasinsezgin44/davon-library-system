import axios from 'axios';

const baseURL = `${process.env.NEXT_PUBLIC_API_BASE_URL || ''}/api`;

const apiClient = axios.create({
  baseURL,
});

// Warn early if API base URL isn't configured
if (typeof window !== 'undefined' && !process.env.NEXT_PUBLIC_API_BASE_URL) {
  // eslint-disable-next-line no-console
  console.warn('[apiClient] NEXT_PUBLIC_API_BASE_URL is not set. Requests will be relative.');
}

apiClient.interceptors.request.use(
  (config) => {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      // Trace outgoing requests for debugging auth header presence
      try {
        const authHeader = (config.headers && (config.headers as any).Authorization) as string | undefined;
        // eslint-disable-next-line no-console
        console.log('[apiClient] Request', {
          baseURL,
          url: config.url,
          method: config.method,
          hasAuth: Boolean(authHeader),
          tokenPrefix: authHeader ? authHeader.substring(0, 16) + '…' : null,
        });
      } catch {}
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default apiClient;

