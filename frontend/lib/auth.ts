// frontend/lib/auth.ts
import { fetchApi } from './api';

export async function login(username, password) {
  const response = await fetchApi('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
  return response;
}
