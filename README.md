# 🐾 Challenge — Pet Service

Microservicio Spring Boot que consume la [Petstore API](https://petstore.swagger.io) a través de **OpenFeign** y expone operaciones REST para crear y consultar mascotas.

---

## 🛠️ Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.1 |
| Spring Cloud OpenFeign | 2025.1.3 |
| Lombok | Latest |
| Logback | (incluido en Spring Boot) |
| Springdoc OpenAPI (Swagger UI) | 3.1.0 |

---

## 🚀 Inicializar el servicio

### Requisitos previos
- Java 17+
- Gradle (o usa el wrapper incluido)

### Clonar y ejecutar

```bash
# Clonar el repositorio
git clone https://github.com/urielmtzb/challenge.git
cd challenge

# Ejecutar con Gradle wrapper
./gradlew bootRun          # Linux / Mac
gradlew.bat bootRun        # Windows
```

### Compilar y ejecutar el JAR

```bash
# Compilar
./gradlew build

# Ejecutar el JAR generado
java -jar build/libs/challenge-0.0.1-SNAPSHOT.jar
```

El servicio levanta por defecto en:
```
http://localhost:8080
```

---

## 📡 Endpoints

### 🐶 Crear mascota

Llama al API externo de Petstore (`POST /pet`), enriquece la respuesta con un `transactionId` (UUIDv4) y `dateCreated` (fecha del sistema).

```
POST /api/v1/pet
Content-Type: application/json
```

**Body:**
```json
{
  "id": 10000023,
  "name": "Firulais",
  "status": "available"
}
```

**Respuesta `201 Created`:**
```json
{
  "id": 10000023,
  "name": "Firulais",
  "status": "available",
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "dateCreated": "2026-09-15T14:31:00"
}
```

---

### 🔍 Consultar mascota por ID

Llama al API externo de Petstore (`GET /pet/{id}`) y retorna la información de la mascota.

```
GET /api/v1/pet/{id}
```

**Ejemplo:**
```
GET /api/v1/pet/10000023
```

**Respuesta `200 OK`:**
```json
{
  "id": 10000023,
  "name": "Firulais",
  "status": "available",
  "transactionId": null,
  "dateCreated": null
}
```

---

## 📄 Documentación (Swagger UI)

La documentación interactiva de la API está disponible una vez el servicio esté corriendo:

| Recurso | URL |
|---|---|
| Swagger UI | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| OpenAPI JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| OpenAPI YAML | [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) |

---

## 📁 Estructura del proyecto

```
src/main/java/com/mb3/challenge/
├── ChallengeApplication.java       # Entry point (@EnableFeignClients)
├── client/
│   └── PetClient.java              # Feign client → Petstore API
├── controller/
│   ├── PetControllerI.java         # Interfaz REST (Swagger annotations)
│   └── PetController.java          # Implementación del controller
├── dto/
│   ├── PetRequest.java             # Body del POST (id, name, status)
│   └── PetResponse.java            # Respuesta enriquecida
├── service/
│   ├── PetServiceI.java            # Contrato del servicio
│   └── PetService.java             # Lógica de negocio + logging
└── exception/
    └── ServiceException.java       # Excepción personalizada

src/main/resources/
├── application.yaml                # Configuración de la app
└── logback-spring.xml              # Configuración de logging
```

---

## ⚙️ Configuración (`application.yaml`)

```yaml
spring:
  application:
    name: challenge

petstore:
  base-url: https://petstore.swagger.io/v2

logging:
  level:
    com.mb3.challenge: DEBUG
    feign: DEBUG
```

---

## 📋 Logging

Los logs se generan en consola y en archivo rotativo (`logs/challenge.log`) con rotación diaria.

Ejemplo de salida al crear una mascota:
```
INFO  PetService - Creando mascota -> id: 10000023, nombre: Firulais, status: available
INFO  PetService - Mascota creada exitosamente -> id: 10000023, nombre: Firulais, status: available, transactionId: 550e8400-..., dateCreated: 2026-09-15T14:31:00
```

---

## 🧪 Pruebas unitarias

### Cobertura de tests

| Clase bajo prueba | Archivo de test | # Tests |
|---|---|---|
| `PetService` | `PetServiceTest` | 7 |
| `PetController` | `PetControllerTest` | 4 |
| `ChallengeApplication` | `ChallengeApplicationTests` | 1 |

### Ejecutar todos los tests

```bash
# Linux / Mac
./gradlew test

# Windows
.\gradlew.bat test
```

### Ejecutar una clase específica

```bash
.\gradlew.bat test --tests "com.mb3.challenge.service.PetServiceTest"
.\gradlew.bat test --tests "com.mb3.challenge.controller.PetControllerTest"
```

### Ejecutar un test específico

```bash
.\gradlew.bat test --tests "com.mb3.challenge.service.PetServiceTest.create_shouldSetTransactionIdAsUuidV4"
```

### Ver reporte HTML

Después de ejecutar los tests, el reporte está disponible en:

```
build/reports/tests/test/index.html
```

```bash
# Abrir en Windows
start build\reports\tests\test\index.html
```

### Estructura de los tests

```
src/test/java/com/mb3/challenge/
├── ChallengeApplicationTests.java     # Context load test
├── service/
│   └── PetServiceTest.java            # @Mock PetClient — lógica de negocio
└── controller/
    └── PetControllerTest.java         # MockMvc standaloneSetup + @Mock PetServiceI
```
