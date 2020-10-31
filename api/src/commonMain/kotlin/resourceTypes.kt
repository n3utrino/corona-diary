package ch.n3utrino.coronadiary.api

import kotlin.js.JsExport


expect class Day {
    val events: Array<Event>
    val metrics: Array<Metric>
}

@JsExport
class Timeline(val days: Array<Day>, val url: String)


@JsExport
data class Event(val title: String)

@JsExport
data class Metric(val value: Double, val type: MetricType)

enum class MetricType {
    INCUBATION, CONTAGIOUS
}