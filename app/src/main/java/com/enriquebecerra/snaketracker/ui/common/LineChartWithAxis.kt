package com.enriquebecerra.snaketracker.ui.common

import android.graphics.Paint
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChartPoint(val date: Long, val value: Float)

/**
 * Gráfica de línea con Canvas nativo de Compose: puntos marcados y etiquetas de fecha
 * en el eje X para un subconjunto de puntos (para no saturar el eje si hay muchos registros).
 * Espera [points] en cualquier orden; internamente se ordenan por fecha ascendente.
 */
@Composable
fun LineChartWithAxis(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier,
    emptyMessage: String = "Registra al menos 2 valores para ver la gráfica"
) {
    val chronological = remember(points) { points.sortedBy { it.date } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (chronological.size < 2) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Box
        }

        val lineColor = MaterialTheme.colorScheme.primary
        val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

        val minValue = chronological.minOf { it.value }
        val maxValue = chronological.maxOf { it.value }
        val range = (maxValue - minValue).takeIf { it > 0f } ?: 1f

        val labelIndices = remember(chronological) { pickLabelIndices(chronological.size) }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val topPadding = 16.dp.toPx()
            val bottomPadding = 28.dp.toPx()
            val chartHeight = size.height - topPadding - bottomPadding
            val chartWidth = size.width
            val stepX = if (chronological.size > 1) chartWidth / (chronological.size - 1) else 0f

            fun xFor(index: Int) = index * stepX
            fun yFor(value: Float): Float {
                val ratio = (value - minValue) / range
                return topPadding + chartHeight - (ratio * chartHeight)
            }

            val gridLines = 3
            repeat(gridLines + 1) { i ->
                val y = topPadding + chartHeight * i / gridLines
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val pointOffsets = chronological.mapIndexed { index, point ->
                Offset(xFor(index), yFor(point.value))
            }

            for (i in 0 until pointOffsets.size - 1) {
                drawLine(
                    color = lineColor,
                    start = pointOffsets[i],
                    end = pointOffsets[i + 1],
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            pointOffsets.forEach { offset ->
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = offset)
            }

            val textPaint = Paint().apply {
                color = labelColor.toArgb()
                textSize = 10.sp.toPx()
                textAlign = Paint.Align.CENTER
            }
            labelIndices.forEach { index ->
                drawContext.canvas.nativeCanvas.drawText(
                    formatShortDate(chronological[index].date),
                    xFor(index),
                    size.height - 6.dp.toPx(),
                    textPaint
                )
            }
        }
    }
}

private fun pickLabelIndices(count: Int): List<Int> {
    if (count <= 4) return (0 until count).toList()
    return listOf(0, (count - 1) / 3, 2 * (count - 1) / 3, count - 1).distinct()
}
