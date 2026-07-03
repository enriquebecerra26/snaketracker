# Changelog

Todas las etapas de desarrollo relevantes de SnakeTracker se documentan en este archivo.

## Etapa 2 - Pantalla principal y formulario de alta

**Fecha:** 2026-07-02

### Añadido

- Pantalla principal (`PetListScreen`) conectada a Room a través de `PetListViewModel`:
  - `LazyColumn` con una card por mascota mostrando foto (o ícono de serpiente como placeholder), nombre, especie y días desde la última alimentación.
  - Cálculo de "días desde la última alimentación" mediante `GetPetListItemsUseCase`, que combina el flujo de mascotas con el de registros de alimentación (`PetRepository` + `FeedingRepository`).
  - `FloatingActionButton` con ícono "+" para navegar al formulario de alta.
  - Empty state con ilustración (ícono de serpiente) y el texto "Agrega tu primera mascota" cuando no hay mascotas registradas.
  - Cada card es navegable a la pantalla de detalle de la mascota.
- Formulario de nueva mascota (`AddPetScreen`):
  - Campos: nombre, especie, fecha de nacimiento (con `DatePicker` de Material 3), peso inicial en gramos (con teclado numérico) y notas opcionales.
  - Validación de campos obligatorios (nombre y especie) con mensajes de error visibles al intentar guardar.
  - Botón "Guardar" que persiste la mascota en Room mediante `SavePetUseCase` y regresa a la lista.
  - Botón "Cancelar" que regresa sin guardar cambios.
- Nuevo modelo de dominio `PetListItem` (mascota + días desde la última alimentación) y consulta `FeedingLogDao.getAllFeedingLogs()` para soportar el cálculo agregado.
- Tema visual personalizado "terrario": paleta oscura de verdes y grises (`ui/theme/Color.kt`, `ui/theme/Theme.kt`) aplicada por defecto en toda la app, y nuevo ícono vectorial de serpiente (`res/drawable/ic_snake.xml`) usado como placeholder de foto e ilustración del empty state.

### Notas técnicas

- El estado de la lista de mascotas se expone como `StateFlow<List<PetListItem>>` en `PetListViewModel`, obtenido con `stateIn` y `SharingStarted.WhileSubscribed(5000)`.
- El tema fuerza colores dinámicos desactivados (`dynamicColor = false`) para mantener la identidad visual de terrario/reptil en todos los dispositivos.

## Etapa 1 - Fundación y estructura

**Fecha:** 2026-07-02

### Añadido

- Configuración inicial del proyecto Android con Gradle Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`).
- Parámetros de compilación: `minSdk 26`, `targetSdk 34`, `compileSdk 34`.
- Integración de Jetpack Compose con Material Design 3 (tema, colores, tipografía).
- Estructura de carpetas siguiendo arquitectura MVVM: `data/`, `domain/`, `ui/`.
- Capa de datos con Room Database:
  - Entidad `Pet` (id, name, species, birthDate, weight, photoUri, notes).
  - Entidad `FeedingLog` (id, petId, date, preyType, preyWeight, accepted, notes).
  - Entidad `WeightLog` (id, petId, date, weight).
  - DAOs (`PetDao`, `FeedingLogDao`, `WeightLogDao`) y `SnakeTrackerDatabase`.
- Capa de dominio con modelos, mappers y casos de uso (use cases) para mascotas, alimentación y peso.
- Repositorios (`PetRepository`, `FeedingRepository`, `WeightRepository`) que exponen los DAOs a la capa de dominio.
- Capa de presentación (UI) con Jetpack Compose:
  - Pantalla de lista de mascotas (`PetListScreen`).
  - Pantalla de alta de mascota (`AddPetScreen`).
  - Pantalla de detalle de mascota (`PetDetailScreen`) con historial de alimentación y peso.
  - Pantalla de registro de alimentación (`AddFeedingScreen`).
  - Pantalla de registro de peso (`AddWeightScreen`).
  - ViewModels asociados a cada pantalla siguiendo el patrón MVVM.
- Navegación entre pantallas con Navigation Component (Navigation Compose).
- Clase `SnakeTrackerApplication` para la inicialización de la base de datos y los repositorios.
- Documentación inicial del proyecto (`README.md`).

### Notas técnicas

- Sin inyección de dependencias externa (Hilt/Koin) en esta etapa: las dependencias se resuelven manualmente desde `SnakeTrackerApplication` mediante una fábrica de `ViewModel` ligera (`snakeTrackerViewModel`).
- Esta etapa sienta las bases estructurales del proyecto; las siguientes etapas incorporarán mejoras de UX, validaciones, pruebas automatizadas y gráficas de evolución de peso.
