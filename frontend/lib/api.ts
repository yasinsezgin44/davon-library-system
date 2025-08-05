// frontend/lib/api.ts

export async function fetchApi(url: string, options: RequestInit = {}) {
  const response = await fetch(`/api${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`API call failed with status ${response.status}`);
  }

  return response.json();
}
