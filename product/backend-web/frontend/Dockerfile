FROM node:18 AS builder
WORKDIR /app
COPY . .
COPY package*.json ./
RUN npm install -g @quasar/cli
RUN npm install
RUN quasar build -m spa

FROM nginx:stable-alpine
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/conf.d/default.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist/spa /usr/share/nginx/html