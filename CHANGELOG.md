# Changelog

Todas las etapas de desarrollo relevantes de SnakeTracker se documentan en este archivo.

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
