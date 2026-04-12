# frontend/Dockerfile

# 1. Build stage
FROM node:18-alpine AS build
WORKDIR /app

ARG REACT_APP_API_URL
ARG REACT_APP_ML_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL
ENV REACT_APP_ML_URL=$REACT_APP_ML_URL

COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 2. Serve with nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
