# SnakeTracker

SnakeTracker es una aplicación Android para el seguimiento y cuidado de reptiles (serpientes y otras mascotas exóticas). Permite registrar mascotas, llevar un historial de alimentación (presas, peso, aceptación) y un historial de peso corporal a lo largo del tiempo.

## Funcionalidades

- Registro de mascotas (nombre, especie, fecha de nacimiento, peso, foto y notas).
- Historial de alimentación por mascota: tipo de presa, peso de la presa, si fue aceptada y notas.
- Historial de peso corporal por mascota para monitorear su crecimiento.
- Navegación entre pantallas: lista de mascotas, detalle, alta de mascota, registro de alimentación y registro de peso.

## Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Build system:** Gradle con Kotlin DSL (`build.gradle.kts`)
- **Persistencia:** Room Database
- **Navegación:** Navigation Component (Navigation Compose)
- **Arquitectura:** MVVM (Model-View-ViewModel) con separación en capas `data/`, `domain/` y `ui/`
- **Concurrencia:** Kotlin Coroutines + Flow
- **Carga de imágenes:** Coil

### Configuración del proyecto

| Propiedad     | Valor |
|---------------|-------|
| `minSdk`      | 26    |
| `targetSdk`   | 34    |
| `compileSdk`  | 34    |

## Estructura del proyecto

```
app/src/main/java/com/enriquebecerra/snaketracker/
├── data/
│   ├── local/
│   │   ├── entity/       # Entidades de Room: Pet, FeedingLog, WeightLog
│   │   ├── dao/          # DAOs de Room
│   │   └── database/     # SnakeTrackerDatabase
│   └── repository/       # Repositorios que exponen los DAOs
├── domain/
│   ├── model/            # Modelos de dominio y mappers
│   └── usecase/          # Casos de uso (lógica de negocio)
├── ui/
│   ├── theme/            # Tema Material 3 (Color, Type, Theme)
│   ├── navigation/       # NavHost y definición de rutas
│   ├── common/           # Utilidades compartidas de UI
│   └── screens/          # Pantallas Compose + ViewModels (petlist, petdetail, addpet, feeding, weight)
└── SnakeTrackerApplication.kt
```

## Modelo de datos

- **Pet**: `id`, `name`, `species`, `birthDate`, `weight`, `photoUri`, `notes`
- **FeedingLog**: `id`, `petId`, `date`, `preyType`, `preyWeight`, `accepted`, `notes`
- **WeightLog**: `id`, `petId`, `date`, `weight`

## Cómo compilar

```bash
./gradlew assembleDebug
```

## Estado del proyecto

Consulta [CHANGELOG.md](CHANGELOG.md) para el detalle de las etapas de desarrollo.
