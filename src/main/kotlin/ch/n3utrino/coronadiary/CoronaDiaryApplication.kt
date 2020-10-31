package ch.n3utrino.coronadiary

import ch.n3utrino.coronadiary.api.Day
import ch.n3utrino.coronadiary.api.Event
import ch.n3utrino.coronadiary.api.Metric
import ch.n3utrino.coronadiary.api.Timeline
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*


@SpringBootApplication
class CoronaDiaryApplication

fun main(args: Array<String>) {
    runApplication<CoronaDiaryApplication>(*args)
}

typealias EventMap = MutableMap<LocalDate, MutableList<Event>>
typealias MetricMap = MutableMap<LocalDate, MutableList<Metric>>

@CrossOrigin
@RestController
class TimelineResource(val timelineRepository: TimelineRepository) {
    @GetMapping("/timeline")
    fun getTimeline(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        contactDate: LocalDate?,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        symptomDate: LocalDate?,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        testDate: LocalDate?
    ): Timeline {


        val days = buildDays(contactDate, symptomDate, testDate)

        val timelineId = UUID.randomUUID().toString()


        timelineRepository.save(TimelineDbo(timelineId, contactDate, symptomDate, testDate))

        return Timeline(days = days.toTypedArray(), url = "/timeline/$timelineId");
    }

    private fun buildDays(contactDate: LocalDate?, symptomDate: LocalDate?, testDate: LocalDate?): List<Day> {
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

    @GetMapping("/timeline/{timelineId}")
    fun getTimelineById(@PathVariable timelineId: String): Timeline {

        val dbo = timelineRepository.findById(timelineId).get()
        val days = buildDays(dbo.contactDate, dbo.symptomDate, dbo.testDate)
        return Timeline(days.toTypedArray(), "timeline/${dbo.id}")

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

}
