import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Em desenvolvimento a SPA chama /api no próprio Vite, que repassa para a API na 8080.
// Assim funciona igual no computador e no Codespaces (onde cada porta tem um endereço próprio).
// O proxy remove o cabeçalho Origin porque quem chama a API, nesse caso, é o servidor do
// Vite e não o navegador. O CORS da API (app.cors.origem) vale para o acesso direto do navegador.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    allowedHosts: ['localhost', '.app.github.dev'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        configure: (proxy) => {
          proxy.on('proxyReq', (req) => req.removeHeader('origin'))
        },
      },
    },
  },
})
