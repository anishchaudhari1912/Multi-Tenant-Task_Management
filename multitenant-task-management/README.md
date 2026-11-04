# Multi-Tenant Task Management System

A beginner-friendly Spring Boot REST API for multiple organizations using one application and one H2 database. Each task and user belongs to a tenant. Tenant identity is read from a signed JWT, never from a request field or URL parameter.

## Technology
- Java 17, Spring Boot 3.3.5, Maven
- Spring Web, Spring Data JPA, Spring Security
- JWT (JJWT), BCrypt password hashing
- H2 in-memory database
- Jakarta Bean Validation, JUnit 5, Mockito, JaCoCo, SonarQube

## Setup and run
1. Install JDK 17 and Maven 3.9+.
2. Extract the ZIP and open the folder in IntelliJ IDEA as a Maven project.
3. Run `mvn clean test` to run unit tests.
4. Run `mvn spring-boot:run` to start the API at `http://localhost:8080`.
5. Health check: `GET http://localhost:8080/health`.
6. H2 console: `http://localhost:8080/h2-console`, JDBC URL `jdbc:h2:mem:taskdb`, username `sa`, blank password.

The application uses `src/main/resources/application.yml`; there is no `application.properties` file. H2 is in-memory, so data resets when the application stops.

## API request flow
1. Register an organization and its first ADMIN using `POST /tenant/register`.
2. Log in using `POST /auth/login`; receive a JWT with `tenant_id` and `role` claims.
3. Send `Authorization: Bearer <token>` for every protected API.
4. ADMIN creates users using `POST /users`.
5. MANAGER creates tasks using `POST /tasks`, then assigns a task using `PUT /tasks/{id}/assign?userId={userId}`.
6. ADMIN and MANAGER can view all tasks belonging to their tenant. USER sees only tasks assigned to them using `GET /tasks`.
7. USER updates the status of an assigned task using `PUT /tasks/{id}/status`.

## API examples

### Register tenant
`POST /tenant/register`
```json
{"tenantName":"Acme Ltd","adminEmail":"admin@acme.com","password":"Admin@123"}
```

### Login
`POST /auth/login`
```json
{"email":"admin@acme.com","password":"Admin@123"}
```

### Create user (ADMIN token)
`POST /users`
```json
{"email":"manager@acme.com","password":"Manager@123","role":"MANAGER"}
```

### Create task (MANAGER token)
`POST /tasks`
```json
{"title":"Prepare API","description":"Complete task endpoints"}
```

### Assign task (MANAGER token)
`PUT /tasks/1/assign?userId=2`

### Update task status (assigned USER token)
`PUT /tasks/1/status`
```json
{"status":"IN_PROGRESS"}
```

### List tasks
`GET /tasks` with an authenticated token.

## Security and tenant isolation
- `/tenant/register` and `/auth/login` are public. All other endpoints require authentication.
- The JWT includes `sub` (email), `tenant_id`, `role`, `iat`, and `exp`.
- `JwtAuthFilter` validates the token and checks the tenant and role claims against the database user record.
- `TenantContext` holds the tenant ID for the current request and is cleared in a `finally` block.
- Service methods receive tenant ID only from `TenantContext` in controllers.
- Repository methods always include tenant ID for task/user lookup and listing. Assignment checks that both task and target user belong to the authenticated tenant.
- USER task listing and status updates are additionally filtered by assigned user ID.
- Login email is unique globally. This is a deliberate assumption because login does not accept tenant ID; it avoids using a request-supplied tenant selector.

## Validation and errors
Request DTOs use `@Valid` and Jakarta validation annotations (`@NotBlank`, `@Email`, `@Size`, `@NotNull`). `GlobalExceptionHandler` returns consistent JSON for validation failures, missing resources, duplicate values, bad credentials, access denial, and unexpected errors.

## Tests and 80% coverage target
Run:
```bash
mvn clean verify
```
JaCoCo generates `target/site/jacoco/index.html` and fails the verify build if instruction coverage is below 80%. The threshold is configured in `pom.xml`. Run tests and inspect this report before submission; the coverage percentage must be confirmed from the generated report on your machine.

## SonarQube
1. Start a local SonarQube Community Build or use your organization's SonarQube server.
2. Create a project and generate a token.
3. Run tests first: `mvn clean verify`.
4. Run analysis (PowerShell):
```powershell
$env:SONAR_TOKEN="your-token"
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=$env:SONAR_TOKEN
```
The project key and JaCoCo report path are configured in `sonar-project.properties` and `pom.xml`.

## Optional Render deployment
A `Dockerfile` and `render.yaml` are included for a simple Render web service. Push the project to GitHub and create a new Blueprint in Render. Set `JWT_SECRET` to a strong secret (Render can generate it). This assignment uses in-memory H2, so deployed data is temporary and is lost when the instance restarts or redeploys; use deployment only as a demonstration unless persistent database storage is configured.

## Assumptions and design decisions
- One database and one codebase serve all tenants.
- The first user registered for a tenant is ADMIN.
- ADMIN creates MANAGER and USER accounts; users cannot create another ADMIN.
- Email addresses are globally unique to keep login simple and avoid accepting tenant ID in the login payload.
- Tenant ID is never accepted from user/task request bodies or query parameters. It comes from the verified JWT.
- Task status values are `TODO`, `IN_PROGRESS`, and `DONE`.
- MANAGER may create and assign tasks only within their tenant.
- ADMIN and MANAGER see all tasks in their tenant; USER sees only assigned tasks and can update only those tasks.
- H2 in-memory database is used because it is explicitly required in the assignment. Data is reset on application restart.
- This is a learning/assignment project; for production, use a persistent database, secret management, HTTPS, rate limiting and stronger operational monitoring.
