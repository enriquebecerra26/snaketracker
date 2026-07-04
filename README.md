# SnakeTracker

SnakeTracker es una aplicación Android completa para el seguimiento y cuidado de serpientes y
otros reptiles. Permite registrar mascotas y llevar el control integral de su alimentación,
biometría (peso y longitud), mudas, defecaciones, salud, condiciones del terrario, reproducción,
gastos y una galería fotográfica con línea del tiempo, con un dashboard de alertas inteligentes y
notificaciones locales.

## Funcionalidades

### Mascotas y perfil
- Registro de mascotas con foto (cámara o galería), nombre, especie, sexo, morfo, fecha de
  nacimiento y de adquisición, criador/tienda, número de chip y notas.
- Edición de perfil en una pantalla dedicada (`EditPetScreen`).
- Dashboard principal con cards por mascota: peso actual y su variación, última comida, próxima
  comida estimada, última muda, última defecación y estado del terrario; más una sección de
  alertas activas.

### Alimentación
- Registro de alimentación con fecha y hora, tipo de presa (chips + opción libre), condición
  (viva/fresca/congelada-descongelada), tamaño, peso de la presa, aceptación y duración.
- Estadísticas de última comida y tasa de aceptación; recordatorio de próxima alimentación
  estimada calculado a partir del promedio histórico.

### Biometría
- Historial de peso con gráfica de línea (Canvas nativo) y variación mensual.
- Historial de longitud con gráfica de línea y crecimiento total.

### Mudas y defecaciones
- Registro de mudas (fases de ojos azules/muda, completitud, problemas, humedad) con promedio de
  días entre mudas.
- Registro de defecaciones con alerta visual si pasan demasiados días sin defecar.

### Salud y terrario
- Historial médico (enfermedad, medicamento, cirugía, desparasitación, tratamiento, visita
  veterinaria) con próxima visita y estado pendiente/resuelto.
- Registro de condiciones del terrario (temperaturas, humedad, sustrato, fuente de calor) con
  alertas visuales si están fuera de rango.

### Reproducción
- Registro de emparejamiento, ovulación, puesta, incubación y eclosión, con selección del macho
  (de la propia app o "Externo"). Visible solo para mascotas hembra o de sexo desconocido.

### Galería
- Fotos por mascota organizadas en grid o línea del tiempo, con filtro por tipo de evento
  (muda/peso/mensual/otro) y visor a pantalla completa.

### Gastos
- Registro de gastos por categoría, asociados a una mascota o generales, con resumen anual y
  gráfica de barras por categoría (Canvas nativo).

### Calendario y alertas
- Calendario mensual (Canvas nativo) con todos los eventos de todas las mascotas codificados por
  color, vista de eventos del día y próximos recordatorios a 7 días.
- Motor de alertas inteligentes (`AlertEngine`) que evalúa alimentación, peso, defecación, mudas,
  salud y terrario, con niveles crítico/advertencia/info.
- Notificaciones locales diarias (WorkManager) que avisan si hay alertas críticas activas.

## Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3 (tema oscuro "terrario")
- **Build system:** Gradle con Kotlin DSL (`build.gradle.kts`)
- **Persistencia:** Room Database (con migraciones incrementales de la v1 a la v6)
- **Navegación:** Navigation Compose, con transiciones fadeIn/fadeOut y splash screen
- **Arquitectura:** MVVM con separación en capas `data/`, `domain/` y `ui/`
- **Concurrencia:** Kotlin Coroutines + Flow (StateFlow reactivo de extremo a extremo)
- **Carga de imágenes:** Coil
- **Gráficas:** Canvas nativo de Compose (líneas y barras, sin librerías externas)
- **Notificaciones:** WorkManager + NotificationCompat

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
│   │   ├── entity/       # Entidades de Room (Pet, FeedingLog, WeightLog, LengthLog,
│   │   │                 # SheddingLog, DefecationLog, HealthRecord, TerrariumLog,
│   │   │                 # ExpenseRecord, PhotoEntry, BreedingRecord)
│   │   ├── dao/          # DAOs de Room
│   │   ├── database/     # SnakeTrackerDatabase y migraciones 1→6
│   │   └── util/         # Utilidades de almacenamiento de fotos
│   └── repository/       # Repositorios que exponen los DAOs
├── domain/
│   ├── model/            # Modelos de dominio, mappers y modelos agregados (Alert,
│   │                     # CalendarEvent, PetDashboardSummary)
│   └── usecase/          # Casos de uso, incluyendo agregadores cross-mascota
│                         # (AlertEngine, GetDashboardSummariesUseCase,
│                         # GetCalendarEventsUseCase)
├── notifications/        # Canal de notificaciones, worker diario y programador
├── ui/
│   ├── theme/            # Tema Material 3 (Color, Type, Theme)
│   ├── navigation/       # NavHost y definición de rutas
│   ├── common/           # Utilidades compartidas de UI (selectores, gráficas, pull-to-refresh)
│   └── screens/          # Pantallas Compose + ViewModels, una carpeta por función
│                         # (petlist, petdetail, addpet, editpet, feeding, weight, length,
│                         # shedding, defecation, health, terrarium, expense, photo,
│                         # breeding, calendar, splash)
└── SnakeTrackerApplication.kt
```

## Cómo compilar

```bash
./gradlew assembleDebug
```

## Estado del proyecto

Proyecto completo (Etapas 1 a 9). Consulta [CHANGELOG.md](CHANGELOG.md) para el detalle de cada
etapa de desarrollo.
