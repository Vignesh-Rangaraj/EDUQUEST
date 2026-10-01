import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { VitePWA } from 'vite-plugin-pwa';

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      registerType: 'autoUpdate',
      injectRegister: 'inline',
      manifestFilename: 'manifest.json',
      includeAssets: ['eduquest.svg'],
      manifest: {
        id: '/',
        name: 'EduQuest - Offline-First Gamified Learning',
        short_name: 'EduQuest',
        description: 'Offline-first learning platform for students.',
        start_url: '/',
        scope: '/',
        display: 'standalone',
        background_color: '#f9fafb',
        theme_color: '#0284c7',
        icons: [{ src: '/eduquest.svg', sizes: 'any', type: 'image/svg+xml', purpose: 'any maskable' }]
      },
      workbox: {
        navigateFallback: '/index.html',
        globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2}'],
        maximumFileSizeToCacheInBytes: 4 * 1024 * 1024,
        runtimeCaching: [
          {
            urlPattern: /\/api\/student\/(?:activities|modules)(?:\/|$)/,
            method: 'GET',
            handler: 'NetworkFirst',
            options: {
              cacheName: 'eduquest-learning-content-v1',
              networkTimeoutSeconds: 5,
              cacheableResponse: { statuses: [200] },
              expiration: { maxEntries: 150, maxAgeSeconds: 24 * 60 * 60 }
            }
          },
          {
            urlPattern: /\/api\/student\/sync$/,
            method: 'POST',
            handler: 'NetworkOnly',
            options: {
              backgroundSync: {
                name: 'eduquest-progress-sync',
                options: { maxRetentionTime: 24 * 60, forceSyncFallback: true }
              }
            }
          }
        ]
      }
    })
  ],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
});
