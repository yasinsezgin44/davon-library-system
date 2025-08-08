import axios from 'axios';

const apiClient = axios.create({
  baseURL: `${process.env.NEXT_PUBLIC_API_BASE_URL}/api`,
});

apiClient.interceptors.request.use(
  (config) => {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      // Trace outgoing requests for debugging auth header presence (no token body)
      try {
        // eslint-disable-next-line no-console
        console.log('[apiClient] Request', {
          url: config.url,
          method: config.method,
          hasAuth: Boolean(config.headers.Authorization),
        });
      } catch {}
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default apiClient;

