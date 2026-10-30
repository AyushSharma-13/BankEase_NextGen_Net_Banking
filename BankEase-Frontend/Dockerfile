# Stage 1: Build the Angular application
FROM node:20-alpine AS build

WORKDIR /app

# Copy dependency manifests
COPY package*.json ./
RUN npm ci

# Copy full source and build
COPY . .
RUN npm run build -- --configuration production

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy compiled assets from Stage 1
COPY --from=build /app/dist/BankEase-Frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
