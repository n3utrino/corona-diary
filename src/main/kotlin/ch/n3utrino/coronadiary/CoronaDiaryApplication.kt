package ch.n3utrino.coronadiary

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

@CrossOrigin
@RestController
class TimelineResource(val timelineRepository: TimelineRepository, val quarantineService: QuarantineService) {
    @GetMapping("/timeline")
    fun getTimeline(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        contactDate: LocalDate?,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        symptomDate: LocalDate?,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        testDate: LocalDate?
    ): Timeline {

        val days = quarantineService.buildDays(contactDate, symptomDate, testDate)
        val timelineId = UUID.randomUUID().toString()
        timelineRepository.save(TimelineDbo(timelineId, contactDate, symptomDate, testDate))

        return Timeline(days = days.toTypedArray(), url = "/timeline/$timelineId");
    }


    @GetMapping("/timeline/{timelineId}")
    fun getTimelineById(@PathVariable timelineId: String): Timeline {

        val dbo = timelineRepository.findById(timelineId).get()
        val days = quarantineService.buildDays(dbo.contactDate, dbo.symptomDate, dbo.testDate)
        return Timeline(days.toTypedArray(), "timeline/${dbo.id}")

    }

}
