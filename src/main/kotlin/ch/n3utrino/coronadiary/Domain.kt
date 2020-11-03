package ch.n3utrino.coronadiary

import ch.n3utrino.coronadiary.api.Day
import ch.n3utrino.coronadiary.api.Event
import ch.n3utrino.coronadiary.api.Metric
import ch.n3utrino.coronadiary.api.MetricType
import org.springframework.stereotype.Service
import java.time.LocalDate


private typealias TimelineDayMap = MutableMap<LocalDate, TimelineDay>

private data class TimelineDay(
    val events: MutableList<Event> = mutableListOf(),
    val metrics: MutableList<Metric> = mutableListOf()
)

@Service
class QuarantineService {

    private fun testDay(date: LocalDate, timelineDayMap: TimelineDayMap): TimelineDayMap {
        return timelineDayMap.appendEvent(date, Event("Positive Test Day"))
    }

    private fun TimelineDayMap.appendEvent(date: LocalDate, event: Event): TimelineDayMap {
        val timelineMaps = this.getOrDefault(date, TimelineDay())
        val eventList = timelineMaps.events
        eventList.add(event)
        this[date] = timelineMaps
        return this
    }

    private fun TimelineDayMap.appendMetric(date: LocalDate, metric: Metric): TimelineDayMap {
        val timelineMaps = this.getOrDefault(date, TimelineDay())
        val eventList = timelineMaps.metrics
        eventList.add(metric)
        this[date] = timelineMaps
        return this
    }

    fun buildDays(contactDate: LocalDate?, symptomDate: LocalDate?, testDate: LocalDate?): List<Day> {
        val days = mutableListOf<Day>()

        var timelineDayMap: TimelineDayMap = mutableMapOf()

        contactDate?.let {
            timelineDayMap = contactEvents(contactDate, timelineDayMap)
        }
        symptomDate?.let { timelineDayMap = symptomDay(symptomDate, timelineDayMap) }
        testDate?.let { timelineDayMap = testDay(testDate, timelineDayMap) }

        for ((date, timelineDay) in timelineDayMap) {
            days.add(Day(date, timelineDay.events.toTypedArray(), timelineDay.metrics.toTypedArray()))
        }

        return days.sorted()
    }

    private fun contactEvents(date: LocalDate, maps: TimelineDayMap): TimelineDayMap {
        maps.appendEvent(date, Event("Contact Day"))
        maps.appendEvent(date.plusDays(5L), Event("Best Test Day"))
        appendQuarantine(maps, date, 10)
        return maps
    }

    private fun appendQuarantine(maps: TimelineDayMap, date: LocalDate, forDays: Int) {
        maps.appendEvent(date, Event("Quarantine Start"))
        repeat(forDays) {
            val nextDate = date.plusDays(it.toLong())
            maps.appendMetric(nextDate, Metric(1.0, MetricType.QUARANTINE))
        }
        maps.appendEvent(date.plusDays(forDays.toLong() + 1), Event("Quarantine End"))
    }

    private fun appendContagiousness(maps: TimelineDayMap, date: LocalDate, forDays: Int) {
        repeat(forDays) {
            val nextDate = date.plusDays(it.toLong())
            maps.appendMetric(nextDate, Metric(1.0.minus(it.div(20.0)), MetricType.CONTAGIOUS))
        }
    }

    private fun symptomDay(date: LocalDate, timelineDayMap: TimelineDayMap): TimelineDayMap {
        timelineDayMap.appendEvent(date, Event("First Symptom Day"))
        appendQuarantine(timelineDayMap, date, 10)
        appendContagiousness(timelineDayMap, date.minusDays(2), 2)
        appendContagiousness(timelineDayMap, date, 14)
        return timelineDayMap
    }

}