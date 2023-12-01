package tdr.aoc.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getInput(year: String, day: String): String = withContext(Dispatchers.IO) {
    val dir = "src/main/resources/$year"
    val path = "$dir/day$day.input"
    if (File(path).exists())
        File(path).readText()
    else {
        Files.createDirectories(Paths.get(dir))
        val cookie = File("src/main/resources/session.ck").readText()

        HttpClient(CIO).get("https://adventofcode.com/$year/day/$day/input") {
            header("Cookie", "session=$cookie")
        }.bodyAsText().let {
            it.split("\n").filter { it.isNotBlank() }.joinToString("\n").also { sanitizedText ->
                File(path).writeText(sanitizedText)
            }
        }
    }
}
