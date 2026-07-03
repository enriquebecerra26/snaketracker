# Changelog

Todas las etapas de desarrollo relevantes de SnakeTracker se documentan en este archivo.

## Etapa 7 - Salud, Terrario y Gastos

**Fecha:** 2026-07-03

### Añadido

- Nuevas entidades Room `HealthRecord` (tipo, título, descripción, veterinario, medicamento/dosis, próxima visita, resuelto), `TerrariumLog` (temperaturas punto caliente/lado frío, humedad, sustrato y su fecha de cambio, fuente de calor) y `ExpenseRecord` (categoría, descripción, monto en MXN, mascota asociada opcional para gastos "generales"). DAOs (`insert`, `getByPetId`/`getAll`, `deleteById`) y repositorios para las tres. `MIGRATION_4_5` (versión 5) crea las tablas nuevas; `ExpenseRecord.petId` usa `ON DELETE SET NULL` para conservar el historial de gastos si se elimina la mascota asociada.
- Nuevos casos de uso para salud, terrario y gastos (`Get`/`Save`/`Delete`), y sus repositorios expuestos desde `SnakeTrackerApplication`.
- Nueva pantalla `AddHealthRecordScreen` (con `AddHealthRecordViewModel`, ruta `add_health/{petId}`): tipo por chips, título obligatorio, fecha, descripción, veterinario, medicamento/dosis (solo si el tipo es Medicamento o Tratamiento), próxima visita opcional y switch de resuelto.
- Nueva pantalla `AddTerrariumLogScreen` (con `AddTerrariumLogViewModel`, ruta `add_terrarium/{petId}`): temperaturas y humedad, sustrato, switch "¿Se cambió el sustrato hoy?" que registra la fecha, fuente de calor y notas.
- Nueva pantalla `AddExpenseScreen` (con `AddExpenseViewModel`, ruta `add_expense`, sin `petId`): fecha, categoría por chips, descripción y monto obligatorios, dropdown de mascota asociada (con opción "General") poblado con `GetPetsUseCase`.
- Nueva pantalla `GastosScreen` (con `GastosViewModel`, ruta `expenses`, accesible desde el ícono "$" en el `TopAppBar` de `PetListScreen`): total anual destacado, gráfica de barras por categoría del año en curso (nuevo componente `BarChart` en `ui/common`, Canvas nativo de Compose), filtro por mascota/General/Todas con chips, y lista de gastos descendente por fecha.
- `PetDetailScreen` ahora tiene 9 tabs: se agregaron **Salud** (después de Defecaciones: chip de color por tipo, badge "Pendiente" si no está resuelto, próxima visita destacada) y **Terrario** (después de Salud: cards de temperatura/humedad del último registro con alertas visuales — rojo si la temperatura del punto caliente está fuera de 28–32°C, naranja si la humedad es menor a 50% — más el historial completo).
- `PetDetailViewModel` ampliado con `healthRecords` y `terrariumLogs` como `StateFlow`.

### Notas técnicas

- Los colores de los chips de tipo de registro de salud (rojo/azul/naranja/etc.) usan valores literales en lugar de roles del `ColorScheme`, ya que actúan como código de color semántico (tipo de evento médico) y no como parte de la identidad visual de la app; el tema oscuro verde/terrario se mantiene en el resto de la UI.
- El resumen anual y la gráfica de gastos por categoría se calculan en el propio `GastosScreen` a partir de la lista completa de `ExpenseRecord` ya expuesta por el ViewModel (filtrando por año), sin necesidad de una consulta SQL adicional.

## Etapa 6 - Alimentación avanzada, mudas y defecaciones

**Fecha:** 2026-07-03

### Añadido

- `FeedingLog` (Room) reconstruido con campos ampliados: `time` (HH:mm), `preyCondition`, `preySize`, `preyWeightGrams` (reemplaza a `preyWeight`) y `durationMinutes`. Nuevas entidades `SheddingLog` (fases de muda, completitud, humedad, problemas) y `DefecationLog` (fecha, tipo, notas), con sus DAOs (`insert`, `getByPetId`, `deleteById`) y repositorios. `MIGRATION_3_4` (versión 4) reconstruye `feeding_logs` preservando los datos existentes (peso y fecha se migran; los campos nuevos toman valores por defecto) y crea `shedding_logs`/`defecation_logs`.
- Nuevos casos de uso `GetSheddingLogsUseCase`/`SaveSheddingLogUseCase`/`DeleteSheddingLogUseCase` y sus equivalentes de `DefecationLog`; nuevos repositorios expuestos desde `SnakeTrackerApplication`.
- `AddFeedingScreen` rehecho: fecha y hora (`DateField` + nuevo `TimeField` con `TimePicker` de Material 3), tipo de presa por chips (Ratón/Rata/ASF/Conejo/Pollito/Otro, con campo libre si se elige "Otro"), condición y tamaño con `SegmentedButton` (nuevo componente genérico `SegmentedSelector`, reutilizado también por `SexSelector`), peso de presa opcional, switch "¿Comió?", duración en minutos (solo visible si comió) y notas. Validación: tipo de presa obligatorio.
- Nuevas pantallas `AddSheddingScreen` (con `AddSheddingViewModel`, ruta `add_shedding/{petId}`) y `AddDefecationScreen` (con `AddDefecationViewModel`, ruta `add_defecation/{petId}`).
- `PetDetailScreen` ahora tiene 7 tabs (con `ScrollableTabRow`): Perfil, **Alimentación** (estadísticas de "última comida" y "tasa de aceptación", tarjetas con fecha+hora/condición/tamaño/peso/duración), **Mudas** (nuevo, entre Alimentación y Defecaciones: promedio de días entre mudas, chip verde/rojo de completitud, días desde la muda anterior), **Defecaciones** (nuevo, entre Mudas y Peso: alerta visual si llevan más de 30 días sin defecar, indicador de color por tipo), Peso, Longitud y Notas.
- Recordatorio inteligente en el header de `PetDetailScreen`: "Próxima alimentación estimada: [fecha]", calculado a partir del promedio de días entre alimentaciones registradas; se resalta en rojo con ícono de alerta si ya pasaron más días que el promedio + 3.

### Notas técnicas

- La migración de `feeding_logs` reconstruye la tabla completa (crear tabla nueva, copiar datos, borrar la vieja, renombrar) en lugar de usar `ALTER TABLE ADD COLUMN`, porque SQLite en Android no soporta renombrar/eliminar columnas de forma portable; `preyWeight` se copia a `preyWeightGrams` y `time`/`preyCondition`/`preySize` reciben valores por defecto razonables para los registros existentes.
- El recordatorio de alimentación, la tasa de aceptación, el promedio entre mudas y la alerta de defecación se calculan como funciones puras sobre las listas ya expuestas por `PetDetailViewModel` (sin StateFlows adicionales), siguiendo el mismo patrón que `calculateAge`/`formatDecimal` ya usado en `PetDetailScreen`.

## Etapa 5 - Biometría completa: peso con variación y longitud

**Fecha:** 2026-07-03

### Añadido

- `WeightLog` (Room) ampliado con `notes: String?`. Nueva entidad `LengthLog` (id, petId, date, lengthCm, notes) con su `LengthLogDao` (`insert`, `getByPetId`, `deleteById`) y `LengthRepository`. `MIGRATION_2_3` (`SnakeTrackerDatabase`, versión 3) agrega la columna de notas a `weight_logs` y crea la tabla `length_logs` sin perder datos existentes.
- Nuevos casos de uso `GetLengthLogsUseCase`, `SaveLengthLogUseCase` y `DeleteLengthLogUseCase`, y `lengthRepository` expuesto desde `SnakeTrackerApplication`.
- `AddWeightScreen`/`AddWeightViewModel` ampliados: fecha con `DatePicker` (por defecto hoy), peso obligatorio con validación y notas opcionales, con botones "Guardar" y "Cancelar".
- Nueva pantalla `AddLengthScreen` (con `AddLengthViewModel`) para registrar longitud en cm, con los mismos campos y validación que el registro de peso. Ruta de navegación `add_length/{petId}`.
- `PetDetailScreen`, tab **Peso**: banner de variación respecto al peso de hace 30 días (`weightVariation` en `PetDetailViewModel`), en verde si aumentó y en rojo si disminuyó; gráfica de línea mejorada con puntos marcados y etiquetas de fecha en el eje X; lista de registros con fecha, peso y notas.
- Nuevo tab **Longitud** en `PetDetailScreen` (entre Peso y Notas): longitud actual y crecimiento total desde el primer registro, gráfica de línea con el historial en cm, y lista de registros con fecha, longitud y notas. Botón flotante "+" hacia `AddLengthScreen`.
- Componente de gráfica compartido `LineChartWithAxis` (`ui/common`, con Canvas nativo de Compose) que reemplaza el `WeightLineChart` específico de la Etapa 3, reutilizado ahora por los tabs de Peso y Longitud.
- `PetDetailViewModel` ampliado con `lengthLogs`, `currentLength` y `weightVariation` como `StateFlow`.

### Notas técnicas

- `weightVariation` se expone como `StateFlow<String>` (tal como se pidió); el signo (`+`/`-`) del texto es lo que determina el color en la UI, evitando exponer un tipo adicional solo para el color.
- Si no hay un registro de hace 30 días o más, `computeWeightVariation` usa el registro más antiguo disponible como referencia; con menos de 2 registros de peso no se muestra el banner.
- Se renombró la función privada `formatWeight` de `PetDetailScreen` a `formatDecimal`, ya que ahora también formatea valores de longitud (cm), no solo peso.

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
