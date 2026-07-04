package com.enriquebecerra.snaketracker.domain.usecase

import java.util.concurrent.TimeUnit

internal fun daysBetween(fromMillis: Long, toMillis: Long): Long =
    TimeUnit.MILLISECONDS.toDays(toMillis - fromMillis)

internal fun daysSinceNow(millis: Long): Int = daysBetween(millis, System.currentTimeMillis()).toInt()

/** Espera fechas en milisegundos ya ordenadas de forma ascendente. */
internal fun averageIntervalDays(datesAscending: List<Long>): Double? {
    if (datesAscending.size < 2) return null
    val intervals = datesAscending.zipWithNext { a, b -> daysBetween(a, b) }.filter { it > 0 }
    if (intervals.isEmpty()) return null
    return intervals.average()
}
