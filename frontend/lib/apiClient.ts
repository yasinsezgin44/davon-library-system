import axios from "axios";

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || "/api",
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // We no longer force a redirect here.
    // The middleware is responsible for protecting pages.
    // Components should handle API errors gracefully.
    return Promise.reject(error);
  }
);

export default apiClient;

