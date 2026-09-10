import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy API requests to the FastAPI backend during development
    proxy: {
      "/auth": "http://localhost:8000",
      "/products": "http://localhost:8000",
      "/cart": "http://localhost:8000",
    },
  },
});
