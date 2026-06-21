package com.example.alwaysdisplay.ui.music

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysdisplay.data.Lyrics
import com.example.alwaysdisplay.data.MusicTrack
import com.example.alwaysdisplay.ui.theme.PrimaryText
import com.example.alwaysdisplay.ui.theme.SecondaryText
import com.example.alwaysdisplay.ui.theme.DimText

/**
 * 全屏音乐页面 - 唱片+歌词+控制栏
 */
@Composable
fun MusicScreen(
    track: MusicTrack?,
    lyrics: Lyrics,
    currentLyricIndex: Int,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMs: Long,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSwipeUp: () -> Unit = {},  // → 原子岛模式
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 100f) onSwipeUp()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 歌曲信息 + 来源
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${track?.displayTitle} - ${track?.displayArtist}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )
            }

            if (track?.source == MusicTrack.Source.PLATFORM) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = getSourceAppName(track.packageName),
                    color = DimText,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 唱片 + 歌词（横屏并排，竖屏上下）
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 唱片
                VinylDisc(
                    coverBitmap = track?.coverBitmap,
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .weight(0.4f),
                    showHalf = true
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 歌词
                if (lyrics.lines.isNotEmpty()) {
                    LyricView(
                        lyrics = lyrics.lines,
                        currentLineIndex = currentLyricIndex,
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                    )
                } else {
                    Text(
                        text = "暂无歌词",
                        color = DimText,
                        modifier = Modifier.weight(0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 播放控制
            PlayerControls(
                isPlaying = isPlaying,
                currentPositionMs = currentPositionMs,
                durationMs = durationMs,
                onPlayPause = onPlayPause,
                onSkipNext = onSkipNext,
                onSkipPrevious = onSkipPrevious,
                onSeekTo = onSeekTo
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "↑ 上滑返回原子岛",
                color = DimText,
                fontSize = 12.sp
            )
        }
    }
}

private fun getSourceAppName(packageName: String): String {
    return when {
        packageName.contains("netease") -> "网易云音乐"
        packageName.contains("qqmusic") || packageName.contains("tencent") -> "QQ音乐"
        packageName.contains("kugou") -> "酷狗音乐"
        packageName.contains("kuwo") -> "酷我音乐"
        packageName.contains("spotify") -> "Spotify"
        packageName.contains("apple") -> "Apple Music"
        else -> "音乐"
    }
}
