package com.example.alwaysdisplay.data

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * LRC歌词解析器
 */
object LyricParser {

    private val timePattern = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})]""")

    fun parse(inputStream: InputStream): Lyrics {
        val lines = mutableListOf<LyricLine>()
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

        reader.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEachLine

            val matches = timePattern.findAll(trimmed)
            val textPart = trimmed.replace(timePattern, "").trim()

            if (textPart.isEmpty()) return@forEachLine

            matches.forEach { match ->
                val (min, sec, ms) = match.destructured
                val timeMs = min.toLong() * 60_000 +
                        sec.toLong() * 1_000 +
                        if (ms.length == 2) ms.toLong() * 10 else ms.toLong()
                lines.add(LyricLine(timeMs = timeMs, text = textPart))
            }
        }

        lines.sortBy { it.timeMs }
        return Lyrics(lines = lines, isSynced = lines.isNotEmpty())
    }

    fun parse(lrcText: String): Lyrics {
        if (lrcText.isBlank()) return Lyrics.Empty
        val lines = mutableListOf<LyricLine>()

        lrcText.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEach

            val matches = timePattern.findAll(trimmed)
            val textPart = trimmed.replace(timePattern, "").trim()

            if (textPart.isEmpty()) return@forEach

            matches.forEach { match ->
                val (min, sec, ms) = match.destructured
                val timeMs = min.toLong() * 60_000 +
                        sec.toLong() * 1_000 +
                        if (ms.length == 2) ms.toLong() * 10 else ms.toLong()
                lines.add(LyricLine(timeMs = timeMs, text = textPart))
            }
        }

        lines.sortBy { it.timeMs }
        return Lyrics(lines = lines, isSynced = lines.isNotEmpty())
    }
}
