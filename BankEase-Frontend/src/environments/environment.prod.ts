export const environment = {
  production: true,
  // Points to Render backend by default or dynamic localStorage override
  apiUrl: (typeof window !== 'undefined' && window.localStorage?.getItem('bankease_backend_url'))
    || 'https://bankease-backend.onrender.com/api'
};
