package com.enriquebecerra.snaketracker.ui.screens.petlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.enriquebecerra.snaketracker.R
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.Alert
import com.enriquebecerra.snaketracker.domain.model.AlertIcon
import com.enriquebecerra.snaketracker.domain.model.AlertType
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.PetDashboardSummary
import com.enriquebecerra.snaketracker.domain.usecase.AlertEngine
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetDashboardSummariesUseCase
import com.enriquebecerra.snaketracker.ui.common.PullToRefreshWrapper
import com.enriquebecerra.snaketracker.ui.common.formatDate
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel
import com.enriquebecerra.snaketracker.ui.theme.SnakeTrackerTheme
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onAddPetClick: () -> Unit,
    onPetClick: (Long) -> Unit,
    onExpensesClick: () -> Unit,
    onCalendarClick: () -> Unit,
    viewModel: PetListViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        PetListViewModel(
            getDashboardSummariesUseCase = GetDashboardSummariesUseCase(
                petRepository = app.petRepository,
                feedingRepository = app.feedingRepository,
                weightRepository = app.weightRepository,
                sheddingRepository = app.sheddingRepository,
                defecationRepository = app.defecationRepository,
                terrariumRepository = app.terrariumRepository
            ),
            alertEngine = AlertEngine(
                petRepository = app.petRepository,
                feedingRepository = app.feedingRepository,
                weightRepository = app.weightRepository,
                sheddingRepository = app.sheddingRepository,
                defecationRepository = app.defecationRepository,
                healthRepository = app.healthRepository,
                terrariumRepository = app.terrariumRepository
            ),
            deletePetUseCase = DeletePetUseCase(app.petRepository)
        )
    }
) {
    val dashboardSummaries by viewModel.dashboardSummaries.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis mascotas") },
                actions = {
                    IconButton(onClick = onCalendarClick) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario")
                    }
                    IconButton(onClick = onExpensesClick) {
                        Icon(Icons.Default.AttachMoney, contentDescription = "Gastos")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPetClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        DashboardContent(
            dashboardSummaries = dashboardSummaries,
            alerts = alerts,
            padding = padding,
            onPetClick = onPetClick
        )
    }
}

@Composable
private fun DashboardContent(
    dashboardSummaries: List<PetDashboardSummary>,
    alerts: List<Alert>,
    padding: PaddingValues,
    onPetClick: (Long) -> Unit
) {
    if (dashboardSummaries.isEmpty()) {
        EmptyPetListState(modifier = Modifier.fillMaxSize().padding(padding))
        return
    }

    PullToRefreshWrapper(modifier = Modifier.padding(padding)) {
        DashboardList(dashboardSummaries = dashboardSummaries, alerts = alerts, onPetClick = onPetClick)
    }
}

@Composable
private fun DashboardList(
    dashboardSummaries: List<PetDashboardSummary>,
    alerts: List<Alert>,
    onPetClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dashboardSummaries, key = { it.pet.id }) { summary ->
            DashboardPetCard(summary = summary, onClick = { onPetClick(summary.pet.id) })
        }
        if (alerts.isNotEmpty()) {
            item {
                Text(
                    text = "Alertas activas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }
            items(alerts) { alert ->
                AlertRow(alert)
            }
        }
    }
}

@Composable
private fun EmptyPetListState(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_snake),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }
            Text(
                text = "Agrega tu primera mascota",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Registra a tu reptil para llevar el control de su alimentación y peso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun DashboardPetCard(summary: PetDashboardSummary, onClick: () -> Unit) {
    val pet = summary.pet
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PetPhoto(photoUri = pet.photoUri)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = pet.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pet.species,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TerrariumStatusIcon(hasAlert = summary.terrariumHasAlert)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            DashboardStatRow(label = "Peso actual") {
                Text(
                    text = "${formatDecimal(summary.currentWeight)} g",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                WeightVariationLabel(summary.weightVariationGrams)
            }
            DashboardStatRow(label = "Última comida") {
                Text(
                    text = daysAgoLabel(summary.daysSinceLastFeeding),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (summary.feedingIsOverdue) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            DashboardStatRow(label = "Próxima comida") {
                Text(
                    text = summary.nextFeedingEstimate?.let { formatDate(it) } ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DashboardStatRow(label = "Última muda") {
                Text(
                    text = daysAgoLabel(summary.daysSinceLastShedding),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DashboardStatRow(label = "Última defecación") {
                Text(
                    text = daysAgoLabel(summary.daysSinceLastDefecation),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = defecationColor(summary.daysSinceLastDefecation)
                )
            }
        }
    }
}

@Composable
private fun DashboardStatRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            content()
        }
    }
}

@Composable
private fun WeightVariationLabel(variationGrams: Double?) {
    if (variationGrams == null) return
    val rounded = variationGrams.roundToInt()
    if (rounded == 0) return
    val color = if (rounded > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val arrow = if (rounded > 0) "↑" else "↓"
    Text(
        text = "$arrow${abs(rounded)}g",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = color
    )
}

@Composable
private fun TerrariumStatusIcon(hasAlert: Boolean) {
    Icon(
        imageVector = if (hasAlert) Icons.Default.Warning else Icons.Default.CheckCircle,
        contentDescription = if (hasAlert) "Terrario con alerta" else "Terrario en rango",
        tint = if (hasAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(22.dp)
    )
}

@Composable
private fun defecationColor(days: Int?): Color = when {
    days == null -> MaterialTheme.colorScheme.onSurface
    days > 30 -> MaterialTheme.colorScheme.error
    days > 21 -> Color(0xFFFFA726)
    else -> MaterialTheme.colorScheme.onSurface
}

@Composable
private fun AlertRow(alert: Alert) {
    val (containerColor, contentColor) = when (alert.type) {
        AlertType.CRITICA -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f) to MaterialTheme.colorScheme.error
        AlertType.ADVERTENCIA -> Color(0xFFFFA726).copy(alpha = 0.15f) to Color(0xFFFFA726)
        AlertType.INFO -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) to MaterialTheme.colorScheme.primary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconForAlert(alert.icon),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = alert.petName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )
        }
    }
}

private fun iconForAlert(icon: AlertIcon): ImageVector = when (icon) {
    AlertIcon.FEEDING -> Icons.Default.Restaurant
    AlertIcon.WEIGHT -> Icons.Default.MonitorWeight
    AlertIcon.SHEDDING -> Icons.Default.Repeat
    AlertIcon.DEFECATION -> Icons.Default.Warning
    AlertIcon.HEALTH -> Icons.Default.MedicalServices
    AlertIcon.TERRARIUM -> Icons.Default.Thermostat
}

@Composable
private fun PetPhoto(photoUri: String?) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_snake),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private fun daysAgoLabel(days: Int?): String = when {
    days == null -> "Sin registros"
    days <= 0 -> "Hoy"
    days == 1 -> "Hace 1 día"
    else -> "Hace $days días"
}

private fun formatDecimal(weight: Double): String =
    if (weight == weight.toLong().toDouble()) weight.toLong().toString() else weight.toString()

@Preview(showBackground = true)
@Composable
private fun PetListPreview() {
    SnakeTrackerTheme {
        DashboardContent(
            dashboardSummaries = listOf(
                PetDashboardSummary(
                    pet = Pet(id = 1, name = "Nagini", species = "Python regius", birthDate = 0L, weight = 850.0),
                    currentWeight = 860.0,
                    weightVariationGrams = 23.0,
                    daysSinceLastFeeding = 6,
                    feedingIsOverdue = false,
                    nextFeedingEstimate = System.currentTimeMillis(),
                    daysSinceLastShedding = 40,
                    daysSinceLastDefecation = 12,
                    terrariumHasAlert = false
                )
            ),
            alerts = emptyList(),
            padding = PaddingValues(0.dp),
            onPetClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetListEmptyPreview() {
    SnakeTrackerTheme {
        DashboardContent(
            dashboardSummaries = emptyList(),
            alerts = emptyList(),
            padding = PaddingValues(0.dp),
            onPetClick = {}
        )
    }
}
