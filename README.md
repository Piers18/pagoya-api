# PagoYa API

> Plataforma fintech de pagos y billetera digital construida con **Spring Boot 4** + **PostgreSQL 16**.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker%20Compose-ready-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-85EA2D?logo=openapiinitiative&logoColor=white)](https://swagger.io/specification/)

---

**Autor:** Henry Antonio Mendoza Puerta — Equipo PagoYa by HampCode

[![YouTube](https://img.shields.io/badge/YouTube-%40hampcode-FF0000?logo=youtube&logoColor=white)](https://www.youtube.com/@hampcode)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-hampcode-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/hampcode/)
[![Email](https://img.shields.io/badge/Email-devacademyweb%40gmail.com-EA4335?logo=gmail&logoColor=white)](mailto:devacademyweb@gmail.com)

---

## Tabla de contenidos

- [Stack](#stack)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Pre-requisitos](#pre-requisitos)
- [Quick start](#quick-start)
- [Configuracion (`.env`)](#configuracion-env)
- [Documentacion del API (Swagger)](#documentacion-del-api-swagger)
- [Coleccion Postman](#coleccion-postman)
- [Comandos utiles](#comandos-utiles)
- [Convenciones del codigo](#convenciones-del-codigo)
- [GitFlow](#gitflow)

---

## Stack

| Capa | Tecnologia |
|---|---|
| Lenguaje | Java 21 (Eclipse Temurin) |
| Framework | Spring Boot 4.0.3 (web, data-jpa, validation) |
| Base de datos | PostgreSQL 16 (Docker) |
| Mapper | MapStruct 1.6.3 |
| Reduccion de boilerplate | Lombok |
| Documentacion del API | springdoc-openapi 2.7.0 (Swagger UI) |
| Build | Maven |
| Infraestructura local | Docker Compose (Postgres + pgAdmin) |

---

## Arquitectura

PagoYa esta organizada como **package by feature**. Cada bounded context vive en su propio paquete y agrupa todas sus capas (controller, service, repository, model, dto, mapper, exception). Los componentes transversales se ubican en `shared/`.

| Bounded context | Modelos / componentes | Ampliaciones futuras posibles | Responsabilidad |
|---|---|---|---|
| `auth/` | `User`, `Role` | `PasswordResetToken`, `RefreshToken`, `LoginAttempt`, `EmailVerificationToken` | Registro, autenticacion y control de sesion. |
| `customer/` | `Customer` | `CustomerDocument` (KYC), `Address`, `CustomerVerification`, `ContactPreference` | Perfiles de cliente y datos personales (DNI, telefono). |
| `account/` | `Account`, `AccountStatus`, `AccountType` | `AccountStatement`, `AccountLimit`, `AccountClosure`, `Beneficiary` | Cuentas digitales: creacion, saldo, listado, reportes. |
| `transfer/` | `Transfer`, `TransferStatus` | `ScheduledTransfer`, `TransferReversal`, `BeneficiaryAccount`, `TransferReceipt` | Transferencias entre cuentas con conversion multi-moneda. |
| `billing/` | `ServiceProvider`, `BillPayment`, `RecurringBillPayment` | `PaymentReceipt`, `Refund`, `BillingAlert`, `FavoritePayment` | Pago de servicios (luz, agua, internet, telefono) y pagos recurrentes programados. |
| `shared/` | `OpenApiConfig`, `AppConfig`, `GlobalExceptionHandler`, `ErrorResponse`, `BusinessRuleException`, `ResourceNotFoundException`, `PageResponse` | `SecurityConfig`, `CacheConfig`, `AuditLog`, `RateLimiter`, `WebClientConfig` | Configuracion transversal, manejo central de errores y paginacion estandar. |

---

## Estructura del proyecto

```
src/main/java/com/hampcode/pagoya/
├── PagoyaApplication.java
├── auth/
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repository/
│   └── service/
├── customer/        ← misma estructura
├── account/         ← misma estructura
├── transfer/        ← misma estructura
└── shared/
    ├── config/      OpenApiConfig, AppConfig
    ├── exception/   GlobalExceptionHandler, ErrorResponse, BusinessRuleException, ResourceNotFoundException
    └── pagination/  PageResponse<T>
```

Pattern por feature:

```
<feature>/
├── controller/   REST endpoints + Swagger annotations
├── service/      I<X>Service (interface) + <X>Service (impl)
├── repository/   Spring Data JPA
├── model/        Entities + enums
├── dto/          Records: <X>Request (entrada) + <X>Response (salida)
├── mapper/       MapStruct
└── exception/    Excepciones especificas del dominio
```

---

## Pre-requisitos

- Java 21
- Maven 3.9+
- Docker Desktop (con Compose)
- Una cuenta de Postman (opcional, para importar la coleccion)

---

## Quick start

```bash
# 1) Levantar Postgres + pgAdmin
docker compose up -d

# 2) Compilar
mvn clean compile

# 3) Cargar variables del .env y arrancar la API
export $(grep -v '^#' .env | xargs) && mvn spring-boot:run
```

Una vez iniciado, abre Swagger UI en:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Configuracion (`.env`)

El proyecto lee las variables desde el archivo `.env` en la raiz:

```env
SPRING_PROFILES_ACTIVE=local
DB_URL=jdbc:postgresql://localhost:55432/pagoya_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Servicios definidos en `compose.yml`:

| Servicio | URL host | Usuario | Password |
|---|---|---|---|
| PostgreSQL (`pagoya_db`) | `localhost:55432` | `postgres` | `postgres` |
| pgAdmin (`pagoya_pgadmin`) | `http://localhost:8082` | `admin@pagoya.com` | `admin` |

Cuando registres el server desde **pgAdmin** (corre dentro de Docker), usa:

| Campo | Valor |
|---|---|
| Host name/address | `postgres` |
| Port | `5432` |
| Maintenance database | `pagoya_db` |
| Username | `postgres` |
| Password | `postgres` |

> **Importante**: el host es `postgres` (el nombre del servicio) porque pgAdmin se conecta por la red interna de Docker. Solo si usas un pgAdmin instalado fuera de Docker, conectas con `localhost:55432`.

### Cargar variables del `.env` segun tu OS

```bash
# macOS / Linux (bash o zsh)
export $(grep -v '^#' .env | xargs) && mvn spring-boot:run
```

```powershell
# Windows PowerShell
Get-Content .env | ForEach-Object {
  if ($_ -match '^\s*([^#=]+)=(.*)$') {
    [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
  }
}
mvn spring-boot:run
```

```cmd
:: Windows CMD
for /f "usebackq tokens=1,2 delims==" %a in (".env") do set %a=%b
mvn spring-boot:run
```

---

## Documentacion del API (Swagger)

Tras arrancar la app:

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

Cada controller esta documentado con `@Tag`, `@Operation` y `@ApiResponses` para que veas en Swagger UI el resumen, los codigos HTTP esperados y un boton **Try it out** para probar en vivo.

---

## Coleccion Postman

El archivo `pagoya-api.postman_collection.json` esta incluido en la raiz del proyecto.

1. Inicia sesion en Postman (asi tu coleccion queda en la nube).
2. Click en **Import** → arrastra el archivo.
3. La coleccion `PagoYa API` aparece con folders por feature (auth, customers, accounts, transfers).
4. Las variables de coleccion (`base_url`, `customer_id`, `account_number`...) ya vienen prellenadas para `localhost`.

---

## Comandos utiles

```bash
# Logs en vivo de los contenedores
docker logs -f pagoya_db
docker logs -f pagoya_pgadmin

# Detener los contenedores (sin borrar datos)
docker compose down

# Detener y borrar volumenes (reset total de la BD)
docker compose down -v

# Compilar sin ejecutar
mvn clean compile

# Empaquetar el JAR
mvn clean package

# Ejecutar tests
mvn test
```

---

## Convenciones del codigo

| Tema | Convencion |
|---|---|
| **DTOs** | Java `record` + Bean Validation. Request DTO valida la entrada; Response DTO oculta campos sensibles e internos. |
| **Mapper** | MapStruct con `@Mapping(ignore = true)` para campos que el service completa (id, FKs, timestamps). |
| **Service** | Interface (`I<X>Service`) + impl. `@Transactional` por metodo: `(readOnly = true)` en lecturas, `@Transactional` en escrituras. |
| **Excepciones** | Custom heredando de `BusinessRuleException` (400) o `ResourceNotFoundException` (404). El `GlobalExceptionHandler` las convierte en `ErrorResponse`. |
| **Mensajes de error** | Sin IDs, DNIs, emails ni datos sensibles. Texto generico (ej: `"cliente no encontrado"`). |
| **Controller** | `@RestController`, `@Tag`, `@Operation`, `@ApiResponses`. Pagina con `@PageableDefault` y devuelve `PageResponse<T>`. |
| **REST** | Verbos HTTP correctos (POST 201, GET 200, DELETE 204), URLs en kebab-case y plural, path params para identidad, query params para filtros. |

---

## GitFlow

**Ramas permanentes:**

| Rama | Rol |
|---|---|
| `main` | Produccion (siempre estable, lo que esta desplegado). |
| `develop` | Integracion (lo que se va probando antes del proximo release). |

**Ramas temporales:**

| Rama | Sale de | Vuelve a | Para que |
|---|---|---|---|
| `feature/<nombre>` | `develop` | `develop` | Nueva funcionalidad. |
| `release/<version>` | `develop` | `main` y `develop` | Preparacion de un release (estabilizar antes de produccion). |
| `hotfix/<nombre>` | **`main`** | **`main` y `develop`** | Fix urgente directo sobre lo que esta en produccion. |

> Importante: `hotfix` parte de `main` (no de `develop`) porque tiene que arreglar lo que esta corriendo en produccion. Despues del merge a `main` se mergea TAMBIEN a `develop` para que el fix no se pierda en el proximo release.

Flujo basico para una nueva feature:

```bash
git checkout develop && git pull origin develop
git checkout -b feature/<nombre>
# ... commits ...
git push -u origin feature/<nombre>
# Abrir Pull Request en GitHub: feature/<nombre> → develop
```

Flujo basico para un hotfix:

```bash
git checkout main && git pull origin main
git checkout -b hotfix/<nombre>
# ... commit del fix ...
git push -u origin hotfix/<nombre>
# Abrir PR: hotfix/<nombre> → main
# Despues del merge a main, mergear tambien a develop:
git checkout develop && git pull origin develop
git merge main && git push origin develop
```
