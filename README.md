# ProductApp
This is a monorepo containing a Spring Boot backend and a React frontend using Vite and MUI.

## Overview
- `backend/`: Spring Boot application providing a CRUD API for products and serving the React app in production.
 - `frontend/`: React application powered by Vite and MUI for a fast, responsive UI.

During development, the React dev server runs separately (with HMR) while Spring Boot serves the API. In production, the React build is bundled into the Spring Boot JAR.

## Prerequisites
- Java 17
- Maven 3.8+
- Node.js 18+ and npm
- Docker & Docker Compose

## Local Development

1. Start LocalStack (DynamoDB) in detached mode:
   ```bash
   docker-compose up -d
   ```

2. Run the backend (Spring Boot):
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   - API base URL: `http://localhost:8080/api/products`
   - To enable remote debugging on port 5005:
     ```bash
     ./mvnw spring-boot:run \
       -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
     ```

3. Run the frontend (Vite):
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   - App URL: `http://localhost:5173`
   - Vite proxies `/api` to `http://localhost:8080` (see `vite.config.js`).

### Proxy Configuration (frontend/vite.config.js)
```js
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  build: {
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true,
  },
})
```

### CORS Configuration (backend/src/main/java/com/example/productapp/config/WebConfig.java)
```java
package com.example.productapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

## Production Build

1. Build the frontend:
   ```bash
   cd frontend
   npm run build
   ```
   This generates a production build in `frontend/dist`.

2. Build the backend (bundles React build into the JAR):
   ```bash
   cd backend
   mvn clean package
   ```

3. Run the application:
   ```bash
   java -jar target/productapp-0.0.1-SNAPSHOT.jar
   ```
   - The Spring Boot app will serve both `/api/**` and static assets for the React app.

## Debugging
- **React**: Use browser DevTools, React Developer Tools, or attach VS Code (Debugger for Chrome) to the Vite server.
- **Spring Boot**: Enable remote debugging (see above) and attach your IDE to port 5005.