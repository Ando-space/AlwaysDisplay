package com.example.alwaysdisplay.data

import android.graphics.Bitmap
import android.net.Uri

/**
 * 统一音乐数据模型，支持本地和平台来源
 */
data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val duration: Long = 0,
    val position: Long = 0,
    val isPlaying: Boolean = false,
    val coverBitmap: Bitmap? = null,
    val coverUri: Uri? = null,
    val source: Source = Source.PLATFORM,
    val packageName: String = "",
    val localUri: Uri? = null
) {
    enum class Source {
        LOCAL,   // 本地音乐
        PLATFORM // 平台音乐（网易云/QQ/Spotify等）
    }

    val displayTitle: String get() = title.ifBlank { "未知歌曲" }
    val displayArtist: String get() = artist.ifBlank { "未知歌手" }
    val progress: Float get() = if (duration > 0) position.toFloat() / duration else 0f
}

/**
 * 歌词行
 */
data class LyricLine(
    val timeMs: Long,
    val text: String
)

/**
 * 歌词数据
 */
data class Lyrics(
    val lines: List<LyricLine>,
    val isSynced: Boolean
) {
    companion object {
        val Empty = Lyrics(lines = emptyList(), isSynced = false)
    }

    fun getLineIndexAt(timeMs: Long): Int {
        if (lines.isEmpty()) return -1
        var index = -1
        for (i in lines.indices) {
            if (lines[i].timeMs <= timeMs) {
                index = i
            } else {
                break
            }
        }
        return index
    }

    fun getCurrentLineText(timeMs: Long): String? {
        val idx = getLineIndexAt(timeMs)
        return if (idx >= 0) lines[idx].text else null
    }
}
