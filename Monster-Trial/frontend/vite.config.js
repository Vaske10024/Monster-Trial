import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

const devSecurityHeaders = {
  'X-Content-Type-Options': 'nosniff',
  'Referrer-Policy': 'strict-origin-when-cross-origin',
  'Permissions-Policy': 'camera=(), microphone=(), geolocation=()'
};

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const backendOrigin = env.VITE_DEV_BACKEND_ORIGIN || 'http://localhost:8080';

  return {
    plugins: [react()],
    server: {
      host: '0.0.0.0',
      port: 5173,
      strictPort: true,
      headers: devSecurityHeaders,
      proxy: {
        '/api': {
          target: backendOrigin,
          changeOrigin: true,
          secure: false
        }
      }
    },
    preview: {
      host: '0.0.0.0',
      port: 4173,
      strictPort: true,
      headers: devSecurityHeaders
    },
    build: {
      sourcemap: false,
      chunkSizeWarningLimit: 600
    }
  };
});
