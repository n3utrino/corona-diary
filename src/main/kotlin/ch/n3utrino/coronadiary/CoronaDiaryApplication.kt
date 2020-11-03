package ch.n3utrino.coronadiary

import ch.n3utrino.coronadiary.api.Timeline
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*
import javax.servlet.http.HttpServletRequest

@SpringBootApplication
class CoronaDiaryApplication

fun main(args: Array<String>) {
    runApplication<CoronaDiaryApplication>(*args)
}

@Controller
class TimelineController(val timelineRepository: TimelineRepository, val quarantineService: QuarantineService) {
    @GetMapping("/timeline/{id}", produces = [MimeTypeUtils.TEXT_HTML_VALUE])
    fun timelineById(@PathVariable id: String, model: Model): String {
        val dbo = timelineRepository.findById(id).get()
        val days = quarantineService.buildDays(dbo.contactDate, dbo.symptomDate, dbo.testDate)
        model.addAttribute("timeline", Timeline(days.toTypedArray(), "timeline/${dbo.id}"))

        return "timeline";
    }

    @GetMapping("/timeline/{id}/{dayIndex}", produces = [MimeTypeUtils.TEXT_HTML_VALUE])
    fun timelineDayById(@PathVariable id: String, @PathVariable dayIndex: Int, model: Model): String {
        val dbo = timelineRepository.findById(id).get()
        val days = quarantineService.buildDays(dbo.contactDate, dbo.symptomDate, dbo.testDate)
        model.addAttribute("timeline", Timeline(days.toTypedArray(), "timeline/${dbo.id}"))
        model.addAttribute("selectedDay", days[dayIndex])

        return "timeline";
    }
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
        testDate: LocalDate?,
        @RequestHeader(value = "User-Agent") userAgent: String,
        request: HttpServletRequest
    ): Timeline {

        val days = quarantineService.buildDays(contactDate, symptomDate, testDate)
        val timelineId = UUID.randomUUID().toString()
        timelineRepository.save(
            TimelineDbo(
                timelineId,
                contactDate,
                symptomDate,
                testDate,
                userAgent,
                request.remoteAddr
            )
        )

        return Timeline(days = days.toTypedArray(), url = "/timeline/$timelineId");
    }


    @GetMapping("/timeline/{timelineId}")
    fun getTimelineById(@PathVariable timelineId: String): Timeline {

        val dbo = timelineRepository.findById(timelineId).get()
        val days = quarantineService.buildDays(dbo.contactDate, dbo.symptomDate, dbo.testDate)
        return Timeline(days.toTypedArray(), "timeline/${dbo.id}")

    }

}
