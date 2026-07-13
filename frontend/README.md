# Frontend - Plataforma de Autenticacion (Angular)

## 1. Vision general

Aplicacion frontend construida con Angular 16 para gestionar autenticacion con:

- Login por credenciales.
- Login SSO y callback.
- Gestion de sesion con JWT + refresh token.
- Navegacion protegida por guard.
- Interceptacion HTTP para adjuntar token y refrescar sesion ante 401.
- Despliegue en contenedor Docker con Nginx y proxy a backend.

El enfoque del proyecto prioriza una base modular, tipada y preparada para escalar por features.

## 2. Stack tecnico

- Angular 16.2.x
- Angular Material 16.2.x
- RxJS 7.8.x
- TypeScript 5.1.x (modo estricto)
- Karma + Jasmine para unit testing
- Docker multi-stage (Node para build + Nginx para runtime)

## 3. Arquitectura del proyecto

Estructura principal:

- src/app/core: configuraciones globales, constantes, guard, interceptor, servicios de sesion y manejo de errores.
- src/app/features/auth: modulo funcional de autenticacion (paginas, layout, componentes, modelos, servicios).
- src/app/layouts: vistas estructurales protegidas.
- src/app/shared: modulos compartidos (ReactiveForms + Angular Material).
- src/app/components/atoms: componentes UI reutilizables (por ejemplo, boton atomico).
- src/assets/scss: sistema SCSS por capas (settings, tokens, tools, generic, elements, components, utilities).
- src/environments: configuracion por entorno (desarrollo/produccion).

Principios aplicados:

- Separacion de responsabilidades (core vs feature vs shared).
- Lazy loading del modulo de autenticacion.
- Tipado fuerte de contratos API.
- Reuso de componentes atomicos para consistencia visual.

## 4. Flujo funcional del programa

### 4.1 Flujo de autenticacion

1. Usuario accede a /auth/login.
2. Completa formulario de email y password o inicia flujo SSO.
3. AuthService llama al backend via /api/auth/*.
4. Si la respuesta contiene tokens, se persisten en localStorage.
5. AuthSessionService marca estado autenticado y redirige a /app/home.
6. El guard permite acceso solo con sesion valida.

### 4.2 Flujo de refresco de token

1. Interceptor agrega Authorization: Bearer para endpoints privados.
2. Ante 401 en endpoint protegido, intenta refresh token.
3. Si refresh es exitoso, reintenta request original con nuevo token.
4. Si refresh falla, limpia sesion y deja al flujo redirigir a login.

### 4.3 Flujo de logout

1. Se toma refresh token almacenado.
2. Se invoca /auth/logout.
3. Independiente del resultado de red, se limpia sesion local.
4. Se navega a /auth/login.

Diagrama de alto nivel:

```mermaid
flowchart LR
	U[Usuario] --> L[Login o SSO]
	L --> A[AuthService]
	A --> B[Backend /api/auth]
	B -->|Tokens| S[TokenStorageService]
	S --> X[AuthSessionService]
	X --> P[/app/home]
	P --> I[JwtInterceptor]
	I -->|401| R[refresh token]
	R -->|ok| P
	R -->|error| Q[clear session + login]
```

## 5. Routing y navegacion

Rutas principales:

- /auth/login: pantalla de login.
- /auth/sso/callback: callback SSO.
- /app/home: area protegida con guard.
- Redirecciones por defecto y wildcard hacia login.

Comportamiento esperado:

- Sin sesion valida: acceso restringido y redireccion a login.
- Con sesion valida: acceso al layout protegido.

## 6. Integracion con API

Configuracion actual:

- Base URL frontend: /api
- Timeout por request: 15000 ms
- Endpoints auth:
	- /auth/login
	- /auth/refresh
	- /auth/logout
	- /auth/sso
	- /auth/sso/callback

Contrato de respuesta esperado:

```ts
ApiResponse<T> {
	success: boolean;
	status: number;
	code?: string;
	message?: string;
	data?: T;
	timestamp: string;
}
```

## 7. Seguridad y sesion

Mecanismos implementados:

- Guard de autenticacion en rutas privadas.
- Interceptor JWT para anexar token solo en endpoints privados del backend.
- Lista de endpoints publicos para evitar token innecesario.
- Refresh de token con control de concurrencia (shareReplay sobre solicitud en curso).
- Limpieza de sesion ante errores de refresh/logout.

Persistencia local:

- access_token
- refresh_token

Nota: actualmente la persistencia se realiza en localStorage.

## 8. UI, temas y estilos

Diseno y componentes:

- Angular Material como base de UI.
- Theme personalizado con paletas primary/accent.
- Componente atomico app-ui-button con estados disabled/loading.
- Layout de login y home protegida con estilo visual propio.

Sistema SCSS:

- Estructura por capas (tokens, tools, generic, components).
- Carga global de:
	- src/assets/scss/custom-material-theme.scss
	- src/styles.scss

## 9. Internacionalizacion (i18n)

El proyecto cuenta con i18n funcional en runtime usando un servicio propio.

Estado actual:

- Idiomas disponibles: en, es, fr, pt (src/assets/i18n).
- Selector de idioma implementado en el login footer.
- Persistencia del idioma seleccionado en localStorage (app_language).
- Fallback de traduccion a ingles cuando una clave no existe en el idioma activo.
- Actualizacion dinamica del atributo html[lang] y del title del documento segun idioma.
- Traducciones aplicadas en login, layout de autenticacion, footer de idioma, home protegida y mensajes de error de autenticacion/sesion.

## 10. Scripts disponibles

En package.json:

- npm start: levanta entorno de desarrollo con proxy (ng serve --proxy-config proxy.conf.json).
- npm run build: compila para produccion en dist/frontend.
- npm run watch: build incremental para desarrollo.
- npm test: ejecuta pruebas unitarias con Karma/Jasmine.

## 11. Ejecucion local

Requisitos:

- Node.js 18+ recomendado.
- npm 9+ recomendado.

Pasos:

1. Instalar dependencias:

	 ```bash
	 npm ci
	 ```

2. Ejecutar en desarrollo:

	 ```bash
	 npm start
	 ```

3. Abrir en navegador:

	 http://localhost:4200

Proxy local:

- /api se redirige a http://localhost:8080 (proxy.conf.json).

## 12. Build y artefactos

Comando de build:

```bash
npm run build
```

Salida:

- Directorio dist/frontend.
- Configuracion por defecto en angular.json orientada a produccion.
- Reemplazo de environment.ts por environment.prod.ts en build productivo.

Validacion realizada:

- Build ejecutado exitosamente.

## 13. Docker y despliegue

El proyecto incluye Dockerfile multi-stage:

1. Stage builder (node:18-alpine): instala dependencias y ejecuta build.
2. Stage runtime (nginx:1.27-alpine): sirve dist/frontend.

Adicional:

- nginx.conf con:
	- fallback SPA (try_files ... /index.html)
	- proxy /api hacia authentication-service:8080
- .dockerignore para reducir contexto (node_modules, dist, coverage, etc.)

Comandos sugeridos:

```bash
docker build -t econom-frontend .
docker run --rm -p 8081:80 econom-frontend
```

Con esto, la app queda disponible en:

- http://localhost:8081

## 14. Calidad de codigo y testing

Configuracion de calidad:

- TypeScript en modo estricto (strict: true).
- Angular strict templates habilitado.
- Reglas adicionales: noImplicitReturns, noFallthroughCasesInSwitch, noPropertyAccessFromIndexSignature.

Pruebas unitarias existentes:

- Componentes de login, formulario, callback SSO, layout auth/protegido y boton UI.

Estado verificado en este repositorio:

- Build: exitoso.
- Unit tests: exitosos (suite en verde).

## 15. Limitaciones y riesgos actuales

- No hay script de lint configurado en package.json.
- No hay pruebas e2e configuradas.
- Existe un enlace de registro en la UI (/auth/register) sin ruta implementada en el modulo actual.

## 16. Mejores practicas y mejoras recomendadas

Prioridad alta:

1. Agregar lint (Angular ESLint) y script npm run lint.
2. Incorporar pipeline CI con etapas minimas: install, lint, test, build, docker build.
3. Cubrir con tests unitarios el servicio i18n (fallback, persistencia y sincronizacion de metadatos).

Prioridad media:

1. Completar flujo de registro o retirar temporalmente enlace en UI.
2. Extender traducciones al resto de features futuras usando el mismo esquema de claves.
3. Anadir pruebas de servicios clave (AuthService, AuthSessionService, JwtInterceptor).

Prioridad evolutiva:

1. End-to-end tests (Cypress o Playwright).
2. Harden de seguridad frontend (CSP, control de XSS, evaluacion de estrategia de almacenamiento de tokens).
3. Observabilidad de frontend (errores de runtime, trazabilidad de llamadas).

## 17. Convenciones de desarrollo

- Estilo de codigo definido por .editorconfig.
- Indentacion de 2 espacios.
- Charset utf-8.
- Estilos de componentes en SCSS.

## 18. Comandos de referencia rapida

```bash
npm ci
npm start
npm run build
npm test
docker build -t econom-frontend .
docker run --rm -p 8081:80 econom-frontend
```

## 19. Estado de madurez

El frontend cuenta con base solida para autenticacion y despliegue contenerizado, con arquitectura modular, tipado estricto e internacionalizacion operativa en runtime. El siguiente salto de madurez se centra en robustecer linting, CI, cobertura de pruebas y completar funcionalidades pendientes del dominio (registro y nuevas features).
