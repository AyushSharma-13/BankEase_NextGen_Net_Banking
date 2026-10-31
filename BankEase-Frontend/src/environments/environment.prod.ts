export const environment = {
  production: true,
  get apiUrl(): string {
    if (typeof window !== 'undefined') {
      const custom = window.localStorage?.getItem('bankease_backend_url');
      if (custom) return custom;
    }
    // Relative '/api' routes through Vercel's reverse proxy directly to Render backend
    return '/api';
  }
};
