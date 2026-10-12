import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";

const here = dirname(fileURLToPath(import.meta.url));

export default defineConfig(({ mode }) => {
  const linkVariant = mode === "production" ? "prod" : "dev";

  return {
    base: "./",
    resolve: {
      alias: {
        "scalajs-main": resolve(here, "build", linkVariant, "main.js"),
      },
    },
    server: {
      port: 5274,
      strictPort: true,
    },
    build: {
      outDir: "dist",
      target: "es2020",
      sourcemap: false,
      emptyOutDir: true,
    },
  };
});
