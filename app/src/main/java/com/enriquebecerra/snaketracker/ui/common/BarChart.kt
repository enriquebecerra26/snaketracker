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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarChartEntry(val label: String, val value: Float)

/**
 * Gráfica de barras simple con Canvas nativo de Compose (sin dependencias externas).
 */
@Composable
fun BarChart(
    entries: List<BarChartEntry>,
    modifier: Modifier = Modifier,
    emptyMessage: String = "Sin datos para mostrar"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (entries.isEmpty() || entries.all { it.value <= 0f }) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Box
        }

        val barColor = MaterialTheme.colorScheme.primary
        val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        val maxValue = entries.maxOf { it.value }.takeIf { it > 0f } ?: 1f

        Canvas(modifier = Modifier.fillMaxSize()) {
            val topPadding = 24.dp.toPx()
            val bottomPadding = 32.dp.toPx()
            val chartHeight = size.height - topPadding - bottomPadding
            val slotWidth = size.width / entries.size
            val barWidth = slotWidth * 0.55f

            val labelPaint = Paint().apply {
                color = labelColor.toArgb()
                textSize = 10.sp.toPx()
                textAlign = Paint.Align.CENTER
            }

            entries.forEachIndexed { index, entry ->
                val barHeight = (entry.value / maxValue) * chartHeight
                val left = index * slotWidth + (slotWidth - barWidth) / 2f
                val top = topPadding + chartHeight - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "$${entry.value.toInt()}",
                    left + barWidth / 2f,
                    (top - 6.dp.toPx()).coerceAtLeast(labelPaint.textSize),
                    labelPaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    entry.label,
                    left + barWidth / 2f,
                    size.height - 10.dp.toPx(),
                    labelPaint
                )
            }
        }
    }
}
