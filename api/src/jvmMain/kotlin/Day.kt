package ch.n3utrino.coronadiary.api

import java.time.LocalDate

actual class Day(val date: LocalDate, actual val events: Array<Event>, actual val metrics: Array<Metric>) :
    Comparable<Day> {
    override fun compareTo(other: Day): Int = this.date.compareTo(other.date)
}