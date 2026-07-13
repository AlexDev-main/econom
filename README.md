# ECONOM

Repositorio full stack con autenticacion JWT, frontend Angular y backend Spring Boot. La orquestacion principal se hace desde la raiz con Docker Compose.

## Resumen rapido

- Frontend: Angular 16 servido con Nginx.
- Backend: Spring Boot (authentication-service).
- Base de datos: PostgreSQL 16.
- Herramienta de gestion DB: pgAdmin.
- Flujo principal: login, refresh, logout y SSO simulado.

## Estructura

- `frontend/`: aplicacion web.
- `backend/authentication-service/`: API de autenticacion.
- `docker-compose.yml`: orquestador unico de todo el stack.

Cada modulo tiene su propia documentacion detallada en su README interno.

## Requisitos

- Docker
- Docker Compose

## Levantar todo el proyecto

Ejecutar desde la raiz del repositorio:

```bash
docker compose up --build -d
```

## Servicios y puertos

- Frontend: http://localhost:4200
- Backend API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432
- pgAdmin: http://localhost:5050

## Comandos utiles

Ver estado de contenedores:

```bash
docker compose ps
```

Ver logs:

```bash
docker compose logs -f
```

Rebuild de un servicio puntual (ejemplo frontend):

```bash
docker compose up -d --build frontend
```

Detener todo:

```bash
docker compose down
```

Detener y limpiar volumenes:

```bash
docker compose down -v
```

## Variables de entorno backend

El backend toma variables desde:

- `backend/authentication-service/.env`
- `backend/authentication-service/.env.example` (referencia)

Si falta `.env`, crearlo a partir de `.env.example`.

## Troubleshooting rapido

- Si no ves cambios en frontend: reconstruir imagen y hacer hard refresh del navegador.
- Si hay conflicto de puertos: verificar procesos o contenedores previos usando `docker compose ps` y `docker ps`.
- Si falla login: revisar logs de frontend y backend con `docker compose logs -f frontend authentication-service`.
