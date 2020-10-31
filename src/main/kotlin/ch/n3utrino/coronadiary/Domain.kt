package ch.n3utrino.coronadiary

import ch.n3utrino.coronadiary.api.Day
import ch.n3utrino.coronadiary.api.Event
import ch.n3utrino.coronadiary.api.Metric
import org.springframework.stereotype.Service
import java.time.LocalDate


private typealias EventMap = MutableMap<LocalDate, MutableList<Event>>
private typealias MetricMap = MutableMap<LocalDate, MutableList<Metric>>

@Service
class QuarantineService {

    private fun testDay(date: LocalDate, eventMap: EventMap): EventMap {
        return eventMap.appendEvent(date, Event("Positive Test Day"))
    }

    private fun EventMap.appendEvent(date: LocalDate, event: Event): EventMap {
        val eventList: MutableList<Event> = this.getOrDefault(date, mutableListOf())
        eventList.add(event)
        this[date] = eventList
        return this
    }

    private fun MetricMap.appendMetric(date: LocalDate, event: Metric): MetricMap {
        val eventList: MutableList<Metric> = this.getOrDefault(date, mutableListOf())
        eventList.add(event)
        this[date] = eventList
        return this
    }

    fun buildDays(contactDate: LocalDate?, symptomDate: LocalDate?, testDate: LocalDate?): List<Day> {
        val days = mutableListOf<Day>()

        var eventMap: EventMap = mutableMapOf()
        var metricMap: MetricMap = mutableMapOf()

        contactDate?.let {
            eventMap = contactEvents(contactDate, eventMap)
        }
        symptomDate?.let { eventMap = symptomDay(symptomDate, eventMap) }
        testDate?.let { eventMap = testDay(testDate, eventMap) }

        for ((date, notes) in eventMap) {
            days.add(Day(date, notes.toTypedArray(), emptyArray()))
        }

        return days.sorted()
    }

    private fun contactEvents(date: LocalDate, eventMap: EventMap): EventMap {
        eventMap.appendEvent(date, Event("Contact Day"))
        eventMap.appendEvent(date, Event("Quarantaine Start"))
        eventMap.appendEvent(date.plusDays(10L), Event("Quarantaine End"))
        return eventMap
    }

    private fun symptomDay(date: LocalDate, eventMap: EventMap): EventMap {
        eventMap.appendEvent(date, Event("First Symptom Day"))
        eventMap.appendEvent(date, Event("Quarantaine Start"))
        return eventMap.appendEvent(date.plusDays(10L), Event("Quarantaine End"))
    }

}