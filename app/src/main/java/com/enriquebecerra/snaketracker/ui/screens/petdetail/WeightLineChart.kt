package com.enriquebecerra.snaketracker.ui.screens.petdetail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.enriquebecerra.snaketracker.domain.model.WeightLog

/**
 * Gráfica de línea simple dibujada con Canvas de Compose (sin dependencias externas).
 * Espera [weightLogs] ordenados de más reciente a más antiguo (como los expone el DAO).
 */
@Composable
fun WeightLineChart(
    weightLogs: List<WeightLog>,
    modifier: Modifier = Modifier
) {
    val chronological = remember(weightLogs) { weightLogs.sortedBy { it.date } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (chronological.size < 2) {
            Text(
                text = "Registra al menos 2 pesos para ver la gráfica",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Box
        }

        val lineColor = MaterialTheme.colorScheme.primary
        val pointColor = MaterialTheme.colorScheme.primary
        val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

        val minWeight = chronological.minOf { it.weight }
        val maxWeight = chronological.maxOf { it.weight }
        val range = (maxWeight - minWeight).takeIf { it > 0.0 } ?: 1.0

        Canvas(modifier = Modifier.fillMaxSize()) {
            val verticalPadding = 12.dp.toPx()
            val chartHeight = size.height - verticalPadding * 2
            val chartWidth = size.width
            val stepX = if (chronological.size > 1) chartWidth / (chronological.size - 1) else 0f

            fun xFor(index: Int) = index * stepX
            fun yFor(weight: Double): Float {
                val ratio = (weight - minWeight) / range
                return verticalPadding + chartHeight - (ratio * chartHeight).toFloat()
            }

            // Líneas de referencia horizontales
            val gridLines = 3
            repeat(gridLines + 1) { i ->
                val y = verticalPadding + chartHeight * i / gridLines
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val points = chronological.mapIndexed { index, log ->
                Offset(xFor(index), yFor(log.weight))
            }

            for (i in 0 until points.size - 1) {
                drawLine(
                    color = lineColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            points.forEach { point ->
                drawCircle(color = pointColor, radius = 5.dp.toPx(), center = point)
            }
        }
    }
}
