package ch.n3utrino.coronadiary

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CoronaDiaryApplication

fun main(args: Array<String>) {
    runApplication<CoronaDiaryApplication>(*args)
}
