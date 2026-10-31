export const environment = {
  production: false,
  get apiUrl(): string {
    if (typeof window !== 'undefined') {
      const custom = window.localStorage?.getItem('bankease_backend_url');
      if (custom) return custom;
    }
    return 'http://localhost:8080/api';
  }
};
