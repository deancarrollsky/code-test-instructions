import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { resolve } from 'path'

export default defineConfig({
    root: "src",
    build: {
        outDir: '../dist'
    },
    plugins: [react()],
})