import axios from 'axios';

const apiClient = axios.create({
  baseURL: `${process.env.NEXT_PUBLIC_API_BASE_URL || ''}/api`,
});

export const setAuthToken = (token: string | null) => {
  if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete apiClient.defaults.headers.common['Authorization'];
  }
};

// Warn early if API base URL isn't configured
if (typeof window !== 'undefined' && !process.env.NEXT_PUBLIC_API_BASE_URL) {
  // eslint-disable-next-line no-console
  console.warn('[apiClient] NEXT_PUBLIC_API_BASE_URL is not set. Requests will be relative.');
}

apiClient.interceptors.request.use(
  (config) => {
    // We only attach the token on the client-side
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('token');
      // eslint-disable-next-line no-console
      console.log('[apiClient] Intercepting request', {
        url: config.url,
        hasToken: !!token,
        headers: config.headers,
      });
    }
    return config;
  },
  (error) => {
    // eslint-disable-next-line no-console
    console.error('[apiClient] Request error', {
      message: error.message,
      config: error.config,
    });
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // eslint-disable-next-line no-console
    console.error('[apiClient] Response error', {
      message: error.message,
      status: error.response?.status,
      data: error.response?.data,
    });
    if (error.response?.status === 401) {
      // eslint-disable-next-line no-console
      console.log('[apiClient] Unauthorized, clearing token');
      localStorage.removeItem('token');
      // Optionally, redirect to login page
      // if (typeof window !== 'undefined') {
      //   window.location.href = '/login';
      // }
    }
    return Promise.reject(error);
  }
);

export default apiClient;

