# Changelog

Todas las etapas de desarrollo relevantes de SnakeTracker se documentan en este archivo.

## Etapa 4 - Perfil completo de serpiente con foto

**Fecha:** 2026-07-03

### Añadido

- Ampliación de la entidad `Pet` (Room) con los campos `sex`, `morph`, `acquisitionDate`, `breeder` y `chipNumber`. Se agregó `MIGRATION_1_2` (`SnakeTrackerDatabase`, versión 2) con `ALTER TABLE` y valores por defecto para no perder los datos existentes; `photoUri` ahora se usa con fotos reales en lugar de `null`.
- `AddPetScreen` ampliado con todos los campos nuevos: morfo (opcional), selector de sexo (Macho/Hembra/Desconocido) con `SegmentedButton` de Material 3, fecha de adquisición opcional, criador/tienda y número de chip.
- Nueva pantalla dedicada `EditPetScreen` (con su propio `EditPetViewModel` y ruta de navegación `edit_pet/{petId}`) que reemplaza el diálogo de edición de la Etapa 3: permite editar el perfil completo de la mascota (incluyendo foto, peso y notas) precargando los valores actuales desde Room.
- Selector de foto reutilizable (`PetPhotoPicker`, en `ui/common`) con preview circular y menú "Cámara" / "Galería":
  - Cámara: `ActivityResultContracts.TakePicture()` con un `Uri` de destino generado vía `FileProvider`.
  - Galería: `ActivityResultContracts.GetContent()`.
  - Ambos flujos piden el permiso correspondiente en tiempo de ejecución (`CAMERA`, y `READ_MEDIA_IMAGES`/`READ_EXTERNAL_STORAGE` según la versión de Android) antes de abrir la cámara o el selector.
  - Las fotos elegidas se copian siempre a almacenamiento interno de la app (`PhotoStorage`, en `data/local/util`) para que el URI persista entre reinicios sin depender de permisos sobre `content://` externos.
- `PetDetailScreen` ahora tiene 4 tabs: "Perfil" (especie, sexo, morfo, fecha de nacimiento con edad calculada, fecha de adquisición, criador y número de chip), "Alimentación", "Peso" y "Notas". El encabezado sigue mostrando la foto real con Coil (o el ícono de serpiente como placeholder). El botón de editar del `TopAppBar` ahora navega a `EditPetScreen` en lugar de abrir un diálogo.
- Permisos nuevos en `AndroidManifest.xml`: `CAMERA`, y `<provider>` de `FileProvider` (`res/xml/file_paths.xml`) para compartir el archivo de la foto de cámara con la app de cámara del sistema. Los permisos de galería (`READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE`) ya existían desde etapas previas.
- Componentes compartidos nuevos en `ui/common`: `DateField` (campo de fecha con `DatePicker` de Material 3, reutilizado en Add/Edit) y `SexSelector`; se extrajo `formatDate` a `ui/common/DateFormat.kt` para eliminar la duplicación entre pantallas.

### Notas técnicas

- Se descartó dejar el `photoUri` como `content://` crudo del selector de galería porque esos permisos pueden expirar entre reinicios de la app; en su lugar siempre se copian los bytes a `filesDir/photos/` y se referencia el archivo con un URI `file://`.
- `EditPetViewModel.savePet` recibe directamente el `Pet` actualizado (construido en la UI con `pet.copy(...)`) en lugar de una lista de parámetros individuales, evitando reconstruir el objeto dos veces.

## Etapa 3 - Pantalla de detalle de mascota

**Fecha:** 2026-07-02

### Añadido

- Pantalla de detalle de mascota (`PetDetailScreen`) completa y conectada a Room vía `PetDetailViewModel`:
  - `TopAppBar` con el nombre de la mascota, botón de regreso, botón de editar perfil (ícono lápiz) y botón de eliminar mascota.
  - Encabezado con foto circular de la mascota (o ícono de serpiente como placeholder), nombre, especie, edad calculada a partir de `birthDate` y peso actual (último `WeightLog` o el peso inicial de `Pet` si no hay registros).
  - Tres pestañas (`TabRow`/`Tab`) debajo del encabezado: "Alimentación", "Peso" y "Notas".
  - Pestaña **Alimentación**: lista (`LazyColumn`) de `FeedingLog` ordenada por fecha descendente, cada tarjeta muestra fecha, tipo de presa, peso de presa e ícono de aceptación (check verde / X roja). `FloatingActionButton` "+" para navegar a `AddFeedingScreen`.
  - Pestaña **Peso**: gráfica de línea simple (`WeightLineChart`) dibujada con `Canvas` nativo de Jetpack Compose (sin dependencias externas) con el historial de peso, y debajo una lista con fecha y peso en gramos de cada registro. `FloatingActionButton` "+" para navegar a `AddWeightScreen`.
  - Pestaña **Notas**: campo de texto editable con las notas generales de la mascota y botón "Guardar notas" que actualiza el registro en Room mediante `SavePetUseCase`.
  - Diálogo de edición de perfil (`EditPetDialog`) accesible desde el ícono de lápiz del `TopAppBar`, con campos de nombre, especie y fecha de nacimiento (`DatePicker` de Material 3), que persiste los cambios con `SavePetUseCase`.
- `PetDetailViewModel` ampliado con `currentWeight` (combinando `pet` y `weightLogs`), `updateNotes()` y `updateProfile()`, reutilizando `SavePetUseCase` para las actualizaciones parciales del perfil.

### Notas técnicas

- Se optó por una gráfica de línea construida con `Canvas` de Compose en lugar de MPAndroidChart para evitar agregar un repositorio JitPack y una dependencia basada en Views (con su respectivo wrapper `AndroidView`); mantiene el enfoque 100% Compose y el theming oscuro de terrario.
- Se mantiene el patrón sin inyección de dependencias externa: los casos de uso adicionales se resuelven manualmente desde `SnakeTrackerApplication` al construir el `ViewModel`.

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
