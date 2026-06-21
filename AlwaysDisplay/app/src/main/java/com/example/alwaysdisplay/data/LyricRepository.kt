package com.example.alwaysdisplay.data

import android.content.Context
import com.example.alwaysdisplay.network.LyricApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 歌词仓库：本地LRC → 内存缓存 → 在线API
 */
class LyricRepository(
    private val apiService: LyricApiService,
    private val context: Context
) {
    private val cache = mutableMapOf<String, Lyrics>()

    suspend fun getLyrics(title: String, artist: String): Lyrics {
        val key = "${title}_${artist}"

        // 1. 内存缓存
        cache[key]?.let { return it }

        // 2. 在线API
        try {
            val result = withContext(Dispatchers.IO) {
                apiService.searchLyrics("$title $artist")
            }
            if (result != null) {
                val synced = result.syncedLyrics
                val plain = result.plainLyrics
                val lrcText = synced ?: plain
                if (!lrcText.isNullOrBlank()) {
                    val lyrics = LyricParser.parse(lrcText)
                    if (lyrics.lines.isNotEmpty()) {
                        cache[key] = lyrics
                        return lyrics
                    }
                }
            }
        } catch (_: Exception) {
            // 静默失败
        }

        return Lyrics.Empty
    }

    fun clearCache() {
        cache.clear()
    }
}
