import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173, // 前端启动端口
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,             // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, '') // 将 /api 前缀去掉
      }
    }
  }
})
