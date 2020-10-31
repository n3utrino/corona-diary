package ch.n3utrino.coronadiary

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.format.annotation.DateTimeFormat
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


data class Timeline(val days: List<Day> = emptyList(), val url: String)
data class Day(val date: LocalDate, val notes: List<Note> = emptyList()) : Comparable<Day> {
    override fun compareTo(other: Day) = date.compareTo(other.date)

}

data class Note(val title: String)

typealias NoteMap = Map<LocalDate, MutableList<Note>>

@RestController
class TimelineResource(val timelineRepository: TimelineRepository) {
    @GetMapping("/timeline")
    fun getTimeline(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            contactDate: LocalDate?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            symptomDate: LocalDate?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            testDate: LocalDate?): Timeline {


        val days = buildDays(contactDate, symptomDate, testDate)

        val timelineId = UUID.randomUUID().toString()


        timelineRepository.save(TimelineDbo(timelineId, contactDate, symptomDate, testDate))

        return Timeline(days = days.sorted(), url = "/timeline/$timelineId");
    }

    private fun buildDays(contactDate: LocalDate?, symptomDate: LocalDate?, testDate: LocalDate?): MutableList<Day> {
        val days = mutableListOf<Day>()

        var noteMap: NoteMap = mapOf()

        contactDate?.let { noteMap = contactDay(contactDate, noteMap) }
        symptomDate?.let { noteMap = symptomtDay(symptomDate, noteMap) }
        testDate?.let { noteMap = testDay(testDate, noteMap) }

        for ((date, notes) in noteMap) {
            days.add(Day(date, notes))
        }

        return days
    }

    @GetMapping("/timeline/{timelineId}")
    fun getTimelineById(@PathVariable timelineId: String): Timeline {

        val dbo = timelineRepository.findById(timelineId).get()
        val days = buildDays(dbo.contactDate, dbo.symptomDate, dbo.testDate)
        return Timeline(days, "timeline/${dbo.id}")

    }


    private fun contactDay(date: LocalDate, noteMap: NoteMap): NoteMap = appendNote(date, Note("Contact Day"), noteMap)
    private fun symptomtDay(date: LocalDate, noteMap: NoteMap): NoteMap = appendNote(date, Note("Symptom Day"), noteMap)
    private fun testDay(date: LocalDate, noteMap: NoteMap): NoteMap {
        return appendNote(date, Note("Test Day"), noteMap)
    }

    private fun appendNote(date: LocalDate, note: Note, noteMap: NoteMap): NoteMap {
        val newMap = noteMap.toMutableMap()
        val noteList: MutableList<Note> = newMap.getOrDefault(date, mutableListOf())
        noteList.add(note)
        newMap[date] = noteList
        return newMap.toMap();
    }

}


