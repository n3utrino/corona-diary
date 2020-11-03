package ch.n3utrino.coronadiary.api

import kotlin.js.Date

@JsExport
actual class Day(
    val date: Date,
    actual val events: Array<Event>,
    actual val metrics: Array<Metric>,
    actual val quarantine: Boolean,
    actual val contagious: Double,
)