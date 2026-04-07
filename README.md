<img width="4488" height="1806" alt="DiagramaEntidadRelación" src="https://github.com/user-attachments/assets/f8a9541b-9051-4571-b58d-1ca86b8a7eae" />

```markdown
# Medical Reservations API

## Descripción general

API REST desarrollada con Java 21 y Spring Boot 4 para gestionar la agenda médica. Permite administrar pacientes, doctores, especialidades, consultorios, tipos de cita, horarios de atención y citas médicas, garantizando reglas de negocio reales, control de disponibilidad y reportes operativos a nivel nacional.

## Stack tecnológico

| Tecnología | Versión | Propósito |
| :--- | :--- | :--- |
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 4.x | Framework backend |
| **PostgreSQL** | Latest | Base de datos relacional |
| **MapStruct** | Latest | Mapeo eficiente entre entidades y DTOs |
| **Lombok** | Latest | Reducción de código repetitivo (boilerplate) |
| **JUnit 5 & Mockito** | Latest | Testing unitario y mocking en capa de servicios |
| **Testcontainers** | Latest | Tests de integración con PostgreSQL real |

## Arquitectura

El proyecto aplica la **arquitectura por capas (N-Layer Architecture)**:

```text
Controller  →  Service  →  Repository  →  Base de datos
               ↑
             Mapper
               ↑
              DTO
```

**Decisión: ¿Por qué arquitectura por capas?**
Se eligió este enfoque porque separa claramente las responsabilidades de cada componente, facilita el testing independiente de cada capa (ej. mockear repositorios para probar servicios) y aísla las entidades de base de datos de los controladores mediante el uso estricto de DTOs.

## Estructura de paquetes

```text
medical-reservations/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── edu/
│   │   │       ├── unimag/
│   │   │           ├── domine/
│   │   │               ├── api/
│   │   │               │   ├── dto/
│   │   │               │       ├── AppointmentDtos.java
│   │   │               │       ├── AppointmentTypesDtos.java
│   │   │               │       ├── AvailabilityDto.java
│   │   │               │       ├── DoctorDtos.java
│   │   │               │       ├── DoctorScheduleDtos.java
│   │   │               │       ├── OfficeDtos.java
│   │   │               │       ├── PatientDtos.java
│   │   │               │       ├── ReportsDtos.java
│   │   │               │       └── SpecialtyDtos.java
│   │   │               ├── entities/
│   │   │               │   ├── enums/
│   │   │               │   │   ├── DayOfWeek.java
│   │   │               │   │   ├── DocumentType.java
│   │   │               │   │   └── Status.java
│   │   │               │   ├── Appointment.java
│   │   │               │   ├── AppointmentType.java
│   │   │               │   ├── Doctor.java
│   │   │               │   ├── DoctorSchedule.java
│   │   │               │   ├── Office.java
│   │   │               │   ├── Patient.java
│   │   │               │   └── Specialty.java
│   │   │               ├── exceptions/
│   │   │               │   ├── BusinessException.java
│   │   │               │   ├── ConflictException.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   └── ValidationException.java
│   │   │               ├── mappers/
│   │   │               │   ├── AppointmentMapper.java
│   │   │               │   ├── AppointmentTypeMapper.java
│   │   │               │   ├── DoctorMapper.java
│   │   │               │   ├── DoctorScheduleMapper.java
│   │   │               │   ├── OfficeMapper.java
│   │   │               │   ├── PatientMapper.java
│   │   │               │   └── SpecialtyMapper.java
│   │   │               ├── repositories/
│   │   │               │   ├── AppointmentRepository.java
│   │   │               │   ├── AppointmentTypeRepository.java
│   │   │               │   ├── DoctorRepository.java
│   │   │               │   ├── DoctorScheduleRepository.java
│   │   │               │   ├── OfficeRepository.java
│   │   │               │   ├── PatientRepository.java
│   │   │               │   └── SpecialtyRepository.java
│   │   │               ├── service/
│   │   │                   ├── implementation/
│   │   │                   │   ├── AppointmentServiceImpl.java
│   │   │                   │   ├── AppointmentTypeServiceImpl.java
│   │   │                   │   ├── AvailabilityServiceImpl.java
│   │   │                   │   ├── DoctorScheduleServiceImpl.java
│   │   │                   │   ├── DoctorServiceImpl.java
│   │   │                   │   ├── OfficeServiceImpl.java
│   │   │                   │   ├── PatientServiceImpl.java
│   │   │                   │   ├── ReportsServiceImpl.java
│   │   │                   │   └── SpecialtyServiceImpl.java
│   │   │                   ├── AppointmentService.java
│   │   │                   ├── AppointmentTypeService.java
│   │   │                   ├── AvailabilityService.java
│   │   │                   ├── DoctorScheduleService.java
│   │   │                   ├── DoctorService.java
│   │   │                   ├── OfficeService.java
│   │   │                   ├── PatientService.java
│   │   │                   ├── ReportsService.java
│   │   │                   └── SpecialtyService.java
│   │   │               └── MedicalReservationsApplication.java
│   │   ├── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.yaml
│   ├── test/
│       ├── java/
│           ├── edu/
│               ├── unimag/
│                   ├── domine/
│                       ├── repositories/
│                       │   ├── AbstractIntegrationDBTest.java
│                       │   ├── AppointmentRepositoryTest.java
│                       │   ├── AppointmentTypeRepositoryTest.java
│                       │   ├── DoctorRepositoryTest.java
│                       │   ├── DoctorScheduleRepositoryTest.java
│                       │   ├── OfficeRepositoryTest.java
│                       │   ├── PatientRepositoryTest.java
│                       │   └── SpecialtyRepositoryTest.java
│                       ├── service/
│                           ├── AppointmentServiceImplTest.java
│                           ├── AppointmentTypeServiceImplTest.java
│                           ├── AvailabilityServiceImplTest.java
│                           ├── DoctorScheduleServiceImplTest.java
│                           ├── DoctorServiceImplTest.java
│                           ├── OfficeServiceImplTest.java
│                           ├── PatientServiceImplTest.java
│                           ├── ReportsServiceImplTest.java
│                           └── SpecialtyServiceImplTest.java
│                       ├── MedicalReservationsApplicationTests.java
│                       ├── TestMedicalReservationsApplication.java
│                       └── TestcontainersConfiguration.java
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Modelo de datos y entidades

**Decisión: Entidades independientes (Sin herencia)**
A diferencia de sistemas que agrupan usuarios genéricos, en este proyecto `Doctor` y `Patient` son entidades completamente independientes. Esto simplifica el modelo relacional, evita problemas de persistencia polimórfica y mejora el rendimiento de las consultas, ya que las reglas de negocio y los campos de médicos y pacientes difieren significativamente.

**Decisión: Gestión de Fechas (LocalDate / LocalTime)**
El sistema utiliza `LocalDate` y `LocalTime` en lugar de timestamps para las citas. La aplicación tiene un alcance nacional (misma zona horaria). Separar fecha y hora permite una manipulación mucho más dinámica en la capa de servicios (ej. cruce de agendas, validación de horarios) sin la sobrecarga de cálculos de offsets.

**Decisión: UUID como identificador**
Se eligió `UUID` en vez de secuencias autoincrementables (`Long`) para evitar exponer el volumen de datos en la API y anular colisiones en la generación de IDs.

### Tablas principales

| Entidad | Descripción |
| :--- | :--- |
| `patients` | Datos del paciente (documento, email, teléfono, etc.). |
| `doctors` | Datos del médico, incluyendo su número de licencia y especialidad. |
| `specialties` | Catálogo de especialidades médicas. |
| `offices` | Consultorios físicos definidos por su nombre y ubicación. |
| `appointment_types` | Tipos de cita con duración predefinida en minutos. |
| `doctor_schedules` | Franjas horarias de disponibilidad por día de la semana. |
| `appointments` | Reservas médicas con trazabilidad, fechas, horas y estados. |

**Trazabilidad:** Todas las citas gestionan su auditoría (`createdAt`, `updatedAt`) de forma programática en la capa de servicio utilizando `Instant.now()`.

## DTOs (Data Transfer Objects)

Se implementaron los DTOs utilizando **Records de Java 21** agrupados por dominio.
* Los records son inmutables, ideales para el transporte de datos.
* Se elimina completamente el código repetitivo de constructores, getters y `equals/hashCode`.
* Implementan `Serializable`.

**DTOs sin entidad propia:** Clases como `AvailabilityDto` y `ReportsDtos` no mapean de forma directa con la base de datos. Son objetos de solo lectura construidos al vuelo por la capa de servicio mediante consultas JPQL agrupadas.

## Mappers (MapStruct)

Para la conversión entre Entidades y DTOs, se optó por MapStruct por su alto rendimiento en tiempo de compilación.

**Actualizaciones Parciales:** Para permitir actualizaciones (Updates) sin sobrescribir con valores nulos, se utiliza la estrategia de ignorar propiedades nulas junto con la protección del identificador principal:

```java
@Mapper(componentModel = "spring")
public interface OfficeMapper {
    OfficeResponse toResponse(Office office);

    @Mapping(target = "id", ignore = true)
    Office toEntity(CreateOfficeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void update(UpdateOfficeRequest dto, @MappingTarget Office entity);
}
```

## Capa de Servicios

Los servicios están divididos en `Interface` e `Implementation` y están anotados con `@Transactional` para garantizar la consistencia de los datos.

### Reglas de Negocio Implementadas (Ej. Creación de Citas)
El método de creación de citas encapsula estrictas reglas de negocio:
1. No se permiten citas en el pasado.
2. Doctor, paciente, consultorio y tipo de cita deben existir y estar activos.
3. El sistema calcula automáticamente la hora de finalización (`endAt`) sumando la duración del tipo de cita a la hora de inicio. El cliente no envía este dato, asegurando consistencia.
4. Validación estricta de solapamiento (Overlap): No pueden chocar horarios para el doctor, el consultorio, ni el paciente simultáneamente.

```java
LocalTime endAt = request.startsAt().plusMinutes(type.getDurationMinutes());
validateDoctorSchedule(doctor.getId(), request.date(), request.startsAt(), endAt);

if (appointmentRepository.existsOverlapByDoctor(doctor.getId(), request.date(), request.startsAt(), endAt)) {
    throw new ConflictException("Doctor overlap");
}
```

### Manejo de Excepciones

| Excepción | HTTP Status | Cuándo se lanza |
| :--- | :--- | :--- |
| `ResourceNotFoundException` | 404 Not Found | Entidad o ID no encontrado en BD. |
| `BusinessException` | 400 Bad Request | Violación de reglas lógicas o entidades inactivas. |
| `ValidationException` | 400 Bad Request | Datos de entrada nulos o formato inválido. |
| `ConflictException` | 409 Conflict | Solapamientos de horario o registros duplicados. |

## Capa de Repositorio y Pruebas

La capa de acceso a datos utiliza **Spring Data JPA**, combinando métodos derivados por convención y consultas JPQL optimizadas. 

### Consultas Destacadas en `AppointmentRepository`
* `existsOverlapByDoctor / Office / Patient`: Valida matemáticamente el cruce de horarios (`startAt < :endAt AND endAt > :startAt`).
* `countCancelledAndNoShowBySpecialty`: Agrupación JPQL para medir el índice de inasistencias.
* `rankingDoctors`: Ordena a los doctores según su productividad (mayor cantidad de citas `COMPLETED`).

### Estrategia de Testing
La suite de pruebas garantiza la integridad del sistema utilizando:
* **Mockito:** Pruebas unitarias de la capa de servicios simulando el comportamiento de los repositorios.
* **Testcontainers:** Pruebas de integración sobre los repositorios levantando una instancia real y efímera de PostgreSQL, evitando las falsas validaciones que ocurren al usar bases de datos en memoria como H2.

## Cómo ejecutar el proyecto

**Requisitos previos:**
* Java 21
* Motor de base de datos PostgreSQL en ejecución
* Docker (Para ejecutar Testcontainers durante las pruebas de integración)

**Paso 1: Configurar la base de datos**
Asegúrate de que tus credenciales coincidan en el archivo `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/medical_reservations
    username: tu_usuario
    password: tu_contraseña
  jpa:
    hibernate:
      ddl-auto: update
```

**Paso 2: Compilar y ejecutar**
Desde la raíz del proyecto, ejecuta en la terminal:

```bash
# Limpiar y compilar el proyecto (omitiendo tests si se requiere)
./mvnw clean install -DskipTests

# Ejecutar la aplicación Spring Boot
./mvnw spring-boot:run

# Ejecutar la suite de pruebas completa
./mvnw test
```
```
