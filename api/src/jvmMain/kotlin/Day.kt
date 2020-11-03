package ch.n3utrino.coronadiary.api

import java.time.LocalDate

actual class Day(val date: LocalDate, actual val events: Array<Event>, actual val metrics: Array<Metric>) :
    Comparable<Day> {

    actual val quarantine: Boolean
        get() {
            return metrics.filter { it.type == MetricType.QUARANTINE }.isNotEmpty()
        }

    actual val contagious: Double
        get() {
            return metrics.firstOrNull { it.type == MetricType.CONTAGIOUS }?.value ?: 0.0
        }


    override fun compareTo(other: Day): Int = this.date.compareTo(other.date)
}