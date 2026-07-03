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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.enriquebecerra.snaketracker.R
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.LengthLog
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetFeedingLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetLengthLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetWeightLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import com.enriquebecerra.snaketracker.ui.common.ChartPoint
import com.enriquebecerra.snaketracker.ui.common.LineChartWithAxis
import com.enriquebecerra.snaketracker.ui.common.formatDate
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState
import java.util.Calendar

private val tabTitles = listOf("Perfil", "Alimentación", "Peso", "Longitud", "Notas")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    onBackClick: () -> Unit,
    onPetDeleted: () -> Unit,
    onEditProfileClick: (Long) -> Unit,
    onAddFeedingClick: (Long) -> Unit,
    onAddWeightClick: (Long) -> Unit,
    onAddLengthClick: (Long) -> Unit,
    viewModel: PetDetailViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        PetDetailViewModel(
            savedStateHandle = handle,
            getPetByIdUseCase = GetPetByIdUseCase(app.petRepository),
            getFeedingLogsUseCase = GetFeedingLogsUseCase(app.feedingRepository),
            getWeightLogsUseCase = GetWeightLogsUseCase(app.weightRepository),
            getLengthLogsUseCase = GetLengthLogsUseCase(app.lengthRepository),
            savePetUseCase = SavePetUseCase(app.petRepository),
            deletePetUseCase = DeletePetUseCase(app.petRepository)
        )
    }
) {
    val pet by viewModel.pet.collectAsStateWithLifecycle()
    val feedingLogs by viewModel.feedingLogs.collectAsStateWithLifecycle()
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()
    val lengthLogs by viewModel.lengthLogs.collectAsStateWithLifecycle()
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
                    onClick = { onAddWeightClick(viewModel.petId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar peso")
                }
                3 -> FloatingActionButton(
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
                PetHeader(pet = currentPet, currentWeight = currentWeight)

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
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
                    2 -> WeightTab(
                        weightLogs = weightLogs,
                        weightVariation = weightVariation,
                        modifier = Modifier.weight(1f)
                    )
                    3 -> LengthTab(
                        lengthLogs = lengthLogs,
                        currentLength = currentLength,
                        modifier = Modifier.weight(1f)
                    )
                    4 -> NotesTab(
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
private fun PetHeader(pet: Pet, currentWeight: Double) {
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
private fun FeedingTab(feedingLogs: List<FeedingLog>, modifier: Modifier = Modifier) {
    if (feedingLogs.isEmpty()) {
        EmptyTabState(text = "Sin registros de alimentación", modifier = modifier)
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(feedingLogs, key = { it.id }) { log ->
            FeedingLogCard(log)
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
                    text = formatDate(log.date),
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
                    text = "${formatDecimal(log.preyWeight)} g",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
