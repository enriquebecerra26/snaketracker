package com.enriquebecerra.snaketracker.ui.screens.petdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.enriquebecerra.snaketracker.R
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.DefecationLog
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.HealthRecord
import com.enriquebecerra.snaketracker.domain.model.LengthLog
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.SheddingLog
import com.enriquebecerra.snaketracker.domain.model.TerrariumLog
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetDefecationLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetFeedingLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetHealthRecordsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetLengthLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetSheddingLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetTerrariumLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetWeightLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import com.enriquebecerra.snaketracker.ui.common.ChartPoint
import com.enriquebecerra.snaketracker.ui.common.LineChartWithAxis
import com.enriquebecerra.snaketracker.ui.common.formatDate
import com.enriquebecerra.snaketracker.ui.common.formatDateTime
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState
import java.util.Calendar
import java.util.concurrent.TimeUnit

private val tabTitles = listOf(
    "Perfil", "Alimentación", "Mudas", "Defecaciones", "Salud", "Terrario", "Peso", "Longitud", "Notas"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    onBackClick: () -> Unit,
    onPetDeleted: () -> Unit,
    onEditProfileClick: (Long) -> Unit,
    onAddFeedingClick: (Long) -> Unit,
    onAddWeightClick: (Long) -> Unit,
    onAddLengthClick: (Long) -> Unit,
    onAddSheddingClick: (Long) -> Unit,
    onAddDefecationClick: (Long) -> Unit,
    onAddHealthClick: (Long) -> Unit,
    onAddTerrariumClick: (Long) -> Unit,
    viewModel: PetDetailViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        PetDetailViewModel(
            savedStateHandle = handle,
            getPetByIdUseCase = GetPetByIdUseCase(app.petRepository),
            getFeedingLogsUseCase = GetFeedingLogsUseCase(app.feedingRepository),
            getWeightLogsUseCase = GetWeightLogsUseCase(app.weightRepository),
            getLengthLogsUseCase = GetLengthLogsUseCase(app.lengthRepository),
            getSheddingLogsUseCase = GetSheddingLogsUseCase(app.sheddingRepository),
            getDefecationLogsUseCase = GetDefecationLogsUseCase(app.defecationRepository),
            getHealthRecordsUseCase = GetHealthRecordsUseCase(app.healthRepository),
            getTerrariumLogsUseCase = GetTerrariumLogsUseCase(app.terrariumRepository),
            savePetUseCase = SavePetUseCase(app.petRepository),
            deletePetUseCase = DeletePetUseCase(app.petRepository)
        )
    }
) {
    val pet by viewModel.pet.collectAsStateWithLifecycle()
    val feedingLogs by viewModel.feedingLogs.collectAsStateWithLifecycle()
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()
    val lengthLogs by viewModel.lengthLogs.collectAsStateWithLifecycle()
    val sheddingLogs by viewModel.sheddingLogs.collectAsStateWithLifecycle()
    val defecationLogs by viewModel.defecationLogs.collectAsStateWithLifecycle()
    val healthRecords by viewModel.healthRecords.collectAsStateWithLifecycle()
    val terrariumLogs by viewModel.terrariumLogs.collectAsStateWithLifecycle()
    val currentWeight by viewModel.currentWeight.collectAsStateWithLifecycle()
    val currentLength by viewModel.currentLength.collectAsStateWithLifecycle()
    val weightVariation by viewModel.weightVariation.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditProfileClick(viewModel.petId) }, enabled = pet != null) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                    }
                    IconButton(onClick = { viewModel.deletePet(onPetDeleted) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar mascota")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            when (selectedTab) {
                1 -> FloatingActionButton(
                    onClick = { onAddFeedingClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar alimentación")
                }
                2 -> FloatingActionButton(
                    onClick = { onAddSheddingClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar muda")
                }
                3 -> FloatingActionButton(
                    onClick = { onAddDefecationClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar defecación")
                }
                4 -> FloatingActionButton(
                    onClick = { onAddHealthClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar salud")
                }
                5 -> FloatingActionButton(
                    onClick = { onAddTerrariumClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar terrario")
                }
                6 -> FloatingActionButton(
                    onClick = { onAddWeightClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar peso")
                }
                7 -> FloatingActionButton(
                    onClick = { onAddLengthClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar longitud")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val currentPet = pet
        if (currentPet == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                PetHeader(pet = currentPet, currentWeight = currentWeight, feedingLogs = feedingLogs)

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 12.dp
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> ProfileTab(pet = currentPet, modifier = Modifier.weight(1f))
                    1 -> FeedingTab(feedingLogs = feedingLogs, modifier = Modifier.weight(1f))
                    2 -> SheddingTab(sheddingLogs = sheddingLogs, modifier = Modifier.weight(1f))
                    3 -> DefecationTab(defecationLogs = defecationLogs, modifier = Modifier.weight(1f))
                    4 -> HealthTab(healthRecords = healthRecords, modifier = Modifier.weight(1f))
                    5 -> TerrariumTab(terrariumLogs = terrariumLogs, modifier = Modifier.weight(1f))
                    6 -> WeightTab(
                        weightLogs = weightLogs,
                        weightVariation = weightVariation,
                        modifier = Modifier.weight(1f)
                    )
                    7 -> LengthTab(
                        lengthLogs = lengthLogs,
                        currentLength = currentLength,
                        modifier = Modifier.weight(1f)
                    )
                    8 -> NotesTab(
                        notes = currentPet.notes,
                        onSave = viewModel::updateNotes,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PetHeader(pet: Pet, currentWeight: Double, feedingLogs: List<FeedingLog>) {
    val feedingReminder = remember(feedingLogs) { computeFeedingReminder(feedingLogs) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (pet.photoUri != null) {
                AsyncImage(
                    model = pet.photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_snake),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Text(
            text = pet.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = pet.species,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeaderStat(label = "Edad", value = calculateAge(pet.birthDate))
            HeaderStat(label = "Peso actual", value = "${formatDecimal(currentWeight)} g")
        }

        feedingReminder?.let { FeedingReminderBanner(it) }
    }
}

@Composable
private fun FeedingReminderBanner(reminder: FeedingReminder) {
    val containerColor = if (reminder.isOverdue) {
        MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (reminder.isOverdue) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (reminder.isOverdue) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = "Próxima alimentación estimada: ${formatDate(reminder.estimatedDate)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (reminder.isOverdue) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor,
            modifier = Modifier.padding(start = if (reminder.isOverdue) 6.dp else 0.dp)
        )
    }
}

@Composable
private fun HeaderStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusChip(text: String, positive: Boolean) {
    val containerColor = if (positive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
    }
    val contentColor = if (positive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = contentColor)
    }
}

@Composable
private fun FeedingTab(feedingLogs: List<FeedingLog>, modifier: Modifier = Modifier) {
    if (feedingLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de alimentación", modifier = modifier)
        return
    }
    val daysSinceLast = remember(feedingLogs) { daysSinceLastFeeding(feedingLogs) }
    val acceptanceRate = remember(feedingLogs) { acceptanceRatePercent(feedingLogs) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeaderStat(
                label = "Última comida",
                value = daysSinceLast?.let { lastFeedingLabel(it) } ?: "—"
            )
            HeaderStat(
                label = "Tasa de aceptación",
                value = acceptanceRate?.let { "$it%" } ?: "—"
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(feedingLogs, key = { it.id }) { log ->
                FeedingLogCard(log)
            }
        }
    }
}

@Composable
private fun FeedingLogCard(log: FeedingLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDateTime(log.date, log.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = log.preyType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${log.preyCondition} • ${log.preySize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                log.preyWeightGrams?.let { weightGrams ->
                    Text(
                        text = "${formatDecimal(weightGrams.toDouble())} g",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (log.accepted) {
                    log.durationMinutes?.let { duration ->
                        Text(
                            text = "Tardó $duration min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (log.accepted) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Aceptó",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Rechazó",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SheddingTab(sheddingLogs: List<SheddingLog>, modifier: Modifier = Modifier) {
    if (sheddingLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de mudas", modifier = modifier)
        return
    }
    val averageInterval = remember(sheddingLogs) { computeAverageSheddingInterval(sheddingLogs) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = averageInterval?.let { "Promedio entre mudas: $it días" } ?: "Registra al menos 2 mudas para ver el promedio",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(sheddingLogs, key = { _, log -> log.id }) { index, log ->
                val daysSincePrevious = sheddingLogs.getOrNull(index + 1)?.let { previous ->
                    TimeUnit.MILLISECONDS.toDays(log.completedDate - previous.completedDate).toInt()
                }
                SheddingLogCard(log, daysSincePrevious)
            }
        }
    }
}

@Composable
private fun SheddingLogCard(log: SheddingLog, daysSincePrevious: Int?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(log.completedDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = daysSincePrevious?.let { "$it días desde la muda anterior" } ?: "Primera muda registrada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!log.problems.isNullOrBlank()) {
                    Text(
                        text = "Problemas: ${log.problems}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            StatusChip(text = if (log.wasComplete) "Completa" else "Incompleta", positive = log.wasComplete)
        }
    }
}

@Composable
private fun DefecationTab(defecationLogs: List<DefecationLog>, modifier: Modifier = Modifier) {
    if (defecationLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de defecaciones", modifier = modifier)
        return
    }
    val daysSinceLast = remember(defecationLogs) {
        defecationLogs.maxByOrNull { it.date }?.let {
            TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - it.date).toInt()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (daysSinceLast != null && daysSinceLast > 30) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Lleva $daysSinceLast días sin defecar",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(defecationLogs, key = { it.id }) { log ->
                DefecationLogRow(log)
            }
        }
    }
}

@Composable
private fun DefecationLogRow(log: DefecationLog) {
    val indicatorColor = when (log.type) {
        "Normal" -> MaterialTheme.colorScheme.primary
        "Uratos" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = formatDate(log.date),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = log.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = indicatorColor
                )
                if (!log.notes.isNullOrBlank()) {
                    Text(
                        text = log.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthTab(healthRecords: List<HealthRecord>, modifier: Modifier = Modifier) {
    if (healthRecords.isEmpty()) {
        EmptyTabState(text = "Sin registros de salud", modifier = modifier)
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(healthRecords, key = { it.id }) { record ->
            HealthRecordCard(record)
        }
    }
}

@Composable
private fun HealthRecordCard(record: HealthRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColoredChip(text = record.type, color = colorForHealthType(record.type))
                if (!record.resolved) {
                    StatusChip(text = "Pendiente", positive = false)
                }
            }
            Text(
                text = formatDate(record.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = record.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!record.description.isNullOrBlank()) {
                Text(
                    text = record.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!record.vetName.isNullOrBlank()) {
                Text(
                    text = "Veterinario: ${record.vetName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!record.medication.isNullOrBlank()) {
                val dosageSuffix = record.dosage?.let { " ($it)" } ?: ""
                Text(
                    text = "Medicamento: ${record.medication}$dosageSuffix",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            record.nextVisitDate?.let { nextVisitDate ->
                Text(
                    text = "Próxima visita: ${formatDate(nextVisitDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ColoredChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

private fun colorForHealthType(type: String): Color = when (type) {
    "Enfermedad" -> Color(0xFFE57373)
    "Visita veterinario" -> Color(0xFF64B5F6)
    "Medicamento" -> Color(0xFFFFB74D)
    "Cirugía" -> Color(0xFFBA68C8)
    "Desparasitación" -> Color(0xFF4DB6AC)
    "Tratamiento" -> Color(0xFF81C784)
    else -> Color(0xFF9E9E9E)
}

private enum class AlertLevel { NONE, WARNING, ERROR }

@Composable
private fun TerrariumTab(terrariumLogs: List<TerrariumLog>, modifier: Modifier = Modifier) {
    if (terrariumLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de terrario", modifier = modifier)
        return
    }
    val latest = remember(terrariumLogs) { terrariumLogs.maxByOrNull { it.date } }

    Column(modifier = modifier.fillMaxWidth()) {
        latest?.let { TerrariumSummaryRow(it) }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(terrariumLogs, key = { it.id }) { log ->
                TerrariumLogCard(log)
            }
        }
    }
}

@Composable
private fun TerrariumSummaryRow(log: TerrariumLog) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val hotAlert = log.hotSpotTemp?.let { it < 28f || it > 32f } ?: false
        TerrariumStatCard(
            icon = Icons.Default.Thermostat,
            label = "Punto caliente",
            value = log.hotSpotTemp?.let { "${formatDecimal(it.toDouble())}°C" } ?: "—",
            alertLevel = if (hotAlert) AlertLevel.ERROR else AlertLevel.NONE,
            modifier = Modifier.weight(1f)
        )
        TerrariumStatCard(
            icon = Icons.Default.Thermostat,
            label = "Lado frío",
            value = log.coldSideTemp?.let { "${formatDecimal(it.toDouble())}°C" } ?: "—",
            alertLevel = AlertLevel.NONE,
            modifier = Modifier.weight(1f)
        )
        val humidityAlert = log.humidityPercent?.let { it < 50 } ?: false
        TerrariumStatCard(
            icon = Icons.Default.WaterDrop,
            label = "Humedad",
            value = log.humidityPercent?.let { "$it%" } ?: "—",
            alertLevel = if (humidityAlert) AlertLevel.WARNING else AlertLevel.NONE,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TerrariumStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    alertLevel: AlertLevel,
    modifier: Modifier = Modifier
) {
    val color = when (alertLevel) {
        AlertLevel.ERROR -> MaterialTheme.colorScheme.error
        AlertLevel.WARNING -> Color(0xFFFFA726)
        AlertLevel.NONE -> MaterialTheme.colorScheme.primary
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = color,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TerrariumLogCard(log: TerrariumLog) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                text = formatDate(log.date),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            log.hotSpotTemp?.let {
                Text(
                    text = "Punto caliente: ${formatDecimal(it.toDouble())}°C",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            log.coldSideTemp?.let {
                Text(
                    text = "Lado frío: ${formatDecimal(it.toDouble())}°C",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            log.humidityPercent?.let {
                Text(
                    text = "Humedad: $it%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!log.substrateType.isNullOrBlank()) {
                Text(
                    text = "Sustrato: ${log.substrateType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (log.substrateChangedDate != null) {
                Text(
                    text = "Sustrato cambiado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!log.heatSource.isNullOrBlank()) {
                Text(
                    text = "Fuente de calor: ${log.heatSource}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!log.notes.isNullOrBlank()) {
                Text(
                    text = log.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeightTab(
    weightLogs: List<WeightLog>,
    weightVariation: String,
    modifier: Modifier = Modifier
) {
    if (weightLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de peso", modifier = modifier)
        return
    }
    Column(modifier = modifier.fillMaxWidth()) {
        if (weightVariation.isNotBlank()) {
            WeightVariationBanner(weightVariation)
        }
        LineChartWithAxis(
            points = weightLogs.map { ChartPoint(it.date, it.weight.toFloat()) },
            modifier = Modifier.padding(top = 8.dp),
            emptyMessage = "Registra al menos 2 pesos para ver la gráfica"
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weightLogs, key = { it.id }) { log ->
                WeightLogRow(log)
            }
        }
    }
}

@Composable
private fun WeightVariationBanner(weightVariation: String) {
    val color = when {
        weightVariation.startsWith("+") -> MaterialTheme.colorScheme.primary
        weightVariation.startsWith("-") -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = weightVariation,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun WeightLogRow(log: WeightLog) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatDate(log.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!log.notes.isNullOrBlank()) {
                Text(
                    text = log.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "${formatDecimal(log.weight)} g",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LengthTab(
    lengthLogs: List<LengthLog>,
    currentLength: Float?,
    modifier: Modifier = Modifier
) {
    if (lengthLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de longitud", modifier = modifier)
        return
    }
    val oldest = remember(lengthLogs) { lengthLogs.minByOrNull { it.date } }
    val growth = if (currentLength != null && oldest != null) currentLength - oldest.lengthCm else null

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HeaderStat(label = "Longitud actual", value = "${formatDecimal(currentLength?.toDouble() ?: 0.0)} cm")
            if (growth != null) {
                HeaderStat(
                    label = "Crecimiento total",
                    value = "${if (growth >= 0) "+" else ""}${formatDecimal(growth.toDouble())} cm"
                )
            }
        }
        LineChartWithAxis(
            points = lengthLogs.map { ChartPoint(it.date, it.lengthCm) },
            emptyMessage = "Registra al menos 2 longitudes para ver la gráfica"
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lengthLogs, key = { it.id }) { log ->
                LengthLogRow(log)
            }
        }
    }
}

@Composable
private fun LengthLogRow(log: LengthLog) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatDate(log.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!log.notes.isNullOrBlank()) {
                Text(
                    text = log.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "${formatDecimal(log.lengthCm.toDouble())} cm",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NotesTab(notes: String?, onSave: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember(notes) { mutableStateOf(notes ?: "") }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Notas generales") },
            placeholder = { Text("Comportamiento, cuidados especiales, historial médico...") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        Button(
            onClick = { onSave(text) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text("Guardar notas")
        }
    }
}

@Composable
private fun EmptyTabState(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileTab(pet: Pet, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ProfileField(label = "Especie", value = pet.species)
        ProfileField(label = "Sexo", value = pet.sex)
        if (pet.morph.isNotBlank()) {
            ProfileField(label = "Morfo", value = pet.morph)
        }
        ProfileField(
            label = "Fecha de nacimiento",
            value = "${formatDate(pet.birthDate)} (${calculateAge(pet.birthDate)})"
        )
        pet.acquisitionDate?.let { acquisitionDate ->
            ProfileField(label = "Fecha de adquisición", value = formatDate(acquisitionDate))
        }
        if (!pet.breeder.isNullOrBlank()) {
            ProfileField(label = "Criador o tienda", value = pet.breeder)
        }
        if (!pet.chipNumber.isNullOrBlank()) {
            ProfileField(label = "Número de chip", value = pet.chipNumber)
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

private data class FeedingReminder(val estimatedDate: Long, val isOverdue: Boolean)

private fun computeFeedingReminder(logs: List<FeedingLog>): FeedingReminder? {
    if (logs.size < 2) return null
    val sorted = logs.sortedBy { it.date }
    val intervalsDays = sorted.zipWithNext { a, b -> TimeUnit.MILLISECONDS.toDays(b.date - a.date) }
        .filter { it > 0 }
    if (intervalsDays.isEmpty()) return null

    val averageDays = intervalsDays.average()
    val lastDate = sorted.last().date
    val estimatedDate = lastDate + TimeUnit.DAYS.toMillis(averageDays.toLong())
    val daysSinceLast = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastDate)
    val isOverdue = daysSinceLast > averageDays + 3
    return FeedingReminder(estimatedDate = estimatedDate, isOverdue = isOverdue)
}

private fun daysSinceLastFeeding(logs: List<FeedingLog>): Int? {
    val last = logs.maxByOrNull { it.date } ?: return null
    return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - last.date).toInt()
}

private fun lastFeedingLabel(days: Int): String = when {
    days <= 0 -> "Hoy"
    days == 1 -> "Hace 1 día"
    else -> "Hace $days días"
}

private fun acceptanceRatePercent(logs: List<FeedingLog>): Int? {
    if (logs.isEmpty()) return null
    return (logs.count { it.accepted } * 100) / logs.size
}

private fun computeAverageSheddingInterval(logs: List<SheddingLog>): Int? {
    if (logs.size < 2) return null
    val sorted = logs.sortedBy { it.completedDate }
    val intervals = sorted.zipWithNext { a, b ->
        TimeUnit.MILLISECONDS.toDays(b.completedDate - a.completedDate)
    }
    if (intervals.isEmpty()) return null
    return intervals.average().toInt()
}

private fun calculateAge(birthDateMillis: Long): String {
    val birth = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
    val now = Calendar.getInstance()

    var years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
    var months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
    if (now.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH)) {
        months -= 1
    }
    if (months < 0) {
        years -= 1
        months += 12
    }
    years = years.coerceAtLeast(0)
    months = months.coerceAtLeast(0)

    return when {
        years <= 0 && months <= 0 -> "Recién nacido"
        years <= 0 -> "$months ${if (months == 1) "mes" else "meses"}"
        months == 0 -> "$years ${if (years == 1) "año" else "años"}"
        else -> "$years ${if (years == 1) "año" else "años"}, $months ${if (months == 1) "mes" else "meses"}"
    }
}

private fun formatDecimal(weight: Double): String =
    if (weight == weight.toLong().toDouble()) weight.toLong().toString() else weight.toString()
