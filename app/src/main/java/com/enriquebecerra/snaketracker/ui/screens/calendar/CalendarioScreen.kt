package com.enriquebecerra.snaketracker.ui.screens.calendar

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.CalendarEvent
import com.enriquebecerra.snaketracker.domain.model.CalendarEventType
import com.enriquebecerra.snaketracker.domain.usecase.GetCalendarEventsUseCase
import com.enriquebecerra.snaketracker.ui.common.PullToRefreshWrapper
import com.enriquebecerra.snaketracker.ui.common.formatDate
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    onBackClick: () -> Unit,
    viewModel: CalendarioViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        CalendarioViewModel(
            GetCalendarEventsUseCase(
                petRepository = app.petRepository,
                feedingRepository = app.feedingRepository,
                weightRepository = app.weightRepository,
                lengthRepository = app.lengthRepository,
                sheddingRepository = app.sheddingRepository,
                defecationRepository = app.defecationRepository,
                healthRepository = app.healthRepository,
                terrariumRepository = app.terrariumRepository,
                expenseRepository = app.expenseRepository
            )
        )
    }
) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }

    val eventsByDay = remember(events) { events.groupBy { millisToLocalDate(it.date) } }
    val upcoming = remember(events) { upcomingEvents(events) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshWrapper(modifier = Modifier.padding(padding)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            MonthHeader(
                yearMonth = displayedMonth,
                onPreviousMonth = {
                    displayedMonth = displayedMonth.minusMonths(1)
                    selectedDay = null
                },
                onNextMonth = {
                    displayedMonth = displayedMonth.plusMonths(1)
                    selectedDay = null
                }
            )
            MonthCalendar(
                yearMonth = displayedMonth,
                eventsByDay = eventsByDay,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                modifier = Modifier.padding(top = 8.dp)
            )
            EventLegend(modifier = Modifier.padding(top = 12.dp))

            selectedDay?.let { day ->
                Text(
                    text = "Eventos del ${formatLocalDate(day)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
                val dayEvents = eventsByDay[day].orEmpty()
                if (dayEvents.isEmpty()) {
                    Text(
                        text = "Sin eventos este día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dayEvents.forEach { event -> EventRow(event) }
                    }
                }
            }

            Text(
                text = "Próximos recordatorios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            if (upcoming.isEmpty()) {
                Text(
                    text = "Sin eventos en los próximos 7 días",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    upcoming.forEach { event -> EventRow(event) }
                }
            }
        }
        }
    }
}

@Composable
private fun MonthHeader(yearMonth: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
        }
        Text(
            text = monthYearLabel(yearMonth),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
private fun MonthCalendar(
    yearMonth: YearMonth,
    eventsByDay: Map<LocalDate, List<CalendarEvent>>,
    selectedDay: LocalDate?,
    onDaySelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val leadingBlanks = firstDayOfMonth.dayOfWeek.value - 1
    val totalCells = leadingBlanks + daysInMonth
    val rows = ceil(totalCells / 7.0).toInt()

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth()) {
            listOf("L", "M", "M", "J", "V", "S", "D").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = onSurfaceVariantColor
                )
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((rows * 52).dp)
                .pointerInput(yearMonth) {
                    detectTapGestures { offset ->
                        val cellWidth = size.width / 7f
                        val cellHeight = size.height / rows.toFloat()
                        val col = (offset.x / cellWidth).toInt().coerceIn(0, 6)
                        val row = (offset.y / cellHeight).toInt().coerceIn(0, rows - 1)
                        val cellIndex = row * 7 + col
                        val day = cellIndex - leadingBlanks + 1
                        if (day in 1..daysInMonth) {
                            onDaySelected(yearMonth.atDay(day))
                        }
                    }
                }
        ) {
            val cellWidth = size.width / 7f
            val cellHeight = size.height / rows.toFloat()
            val dayPaint = Paint().apply {
                textSize = 13.sp.toPx()
                textAlign = Paint.Align.CENTER
            }

            for (day in 1..daysInMonth) {
                val cellIndex = leadingBlanks + day - 1
                val row = cellIndex / 7
                val col = cellIndex % 7
                val cx = col * cellWidth + cellWidth / 2f
                val cy = row * cellHeight + cellHeight * 0.4f
                val date = yearMonth.atDay(day)
                val isSelected = date == selectedDay
                val isToday = date == today

                if (isSelected) {
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.25f),
                        radius = cellWidth * 0.32f,
                        center = Offset(cx, cy - 4.dp.toPx())
                    )
                }
                dayPaint.color = if (isToday) primaryColor.toArgb() else onSurfaceColor.toArgb()
                dayPaint.isFakeBoldText = isToday
                drawContext.canvas.nativeCanvas.drawText(day.toString(), cx, cy, dayPaint)

                val dayEvents = eventsByDay[date].orEmpty()
                val distinctTypes = dayEvents.map { it.type }.distinct().take(4)
                if (distinctTypes.isNotEmpty()) {
                    val dotRadius = 3.dp.toPx()
                    val dotSpacing = 9.dp.toPx()
                    val totalWidth = (distinctTypes.size - 1) * dotSpacing
                    var dotX = cx - totalWidth / 2f
                    val dotY = row * cellHeight + cellHeight * 0.72f
                    distinctTypes.forEach { type ->
                        drawCircle(color = colorForEventType(type), radius = dotRadius, center = Offset(dotX, dotY))
                        dotX += dotSpacing
                    }
                }
            }
        }
    }
}

@Composable
private fun EventLegend(modifier: Modifier = Modifier) {
    val items = listOf(
        "Alimentación" to CalendarEventType.FEEDING,
        "Peso/Longitud" to CalendarEventType.BIOMETRICS,
        "Muda" to CalendarEventType.SHEDDING,
        "Defecación" to CalendarEventType.DEFECATION,
        "Salud" to CalendarEventType.HEALTH,
        "Terrario" to CalendarEventType.TERRARIUM,
        "Gasto" to CalendarEventType.EXPENSE
    )
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { (label, type) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                LegendDot(colorForEventType(type))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(
        modifier = Modifier.size(8.dp).clip(CircleShape).background(color)
    )
}

@Composable
private fun EventRow(event: CalendarEvent) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LegendDot(colorForEventType(event.type))
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = listOfNotNull(event.petName, formatDate(event.date)).joinToString(" • "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun colorForEventType(type: CalendarEventType): Color = when (type) {
    CalendarEventType.FEEDING -> Color(0xFF66BB6A)
    CalendarEventType.BIOMETRICS -> Color(0xFF42A5F5)
    CalendarEventType.SHEDDING -> Color(0xFFAB47BC)
    CalendarEventType.DEFECATION -> Color(0xFFFFA726)
    CalendarEventType.HEALTH -> Color(0xFFEF5350)
    CalendarEventType.TERRARIUM -> Color(0xFF9E9E9E)
    CalendarEventType.EXPENSE -> Color(0xFFFFEE58)
}

private fun monthYearLabel(yearMonth: YearMonth): String {
    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { it.uppercase() }
    return "$monthName ${yearMonth.year}"
}

private fun millisToLocalDate(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

private fun formatLocalDate(date: LocalDate): String =
    "%02d/%02d/%04d".format(date.dayOfMonth, date.monthValue, date.year)

private fun upcomingEvents(events: List<CalendarEvent>): List<CalendarEvent> {
    val now = System.currentTimeMillis()
    val sevenDaysFromNow = now + TimeUnit.DAYS.toMillis(7)
    return events.filter { it.date in now..sevenDaysFromNow }.sortedBy { it.date }
}
