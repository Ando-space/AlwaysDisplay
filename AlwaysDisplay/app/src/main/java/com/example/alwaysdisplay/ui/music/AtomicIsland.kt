package com.example.alwaysdisplay.ui.music

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysdisplay.media.MediaSessionBridge
import com.example.alwaysdisplay.ui.theme.IslandBackground
import com.example.alwaysdisplay.ui.theme.PrimaryText
import com.example.alwaysdisplay.ui.theme.SecondaryText
import com.example.alwaysdisplay.ui.theme.DimText
import com.example.alwaysdisplay.ui.theme.ProgressActive
import com.example.alwaysdisplay.ui.theme.ProgressInactive

/**
 * 原子岛组件 - 类vivo原子岛风格的音乐信息展示
 */
@Composable
fun AtomicIsland(
    mediaInfo: MediaSessionBridge.MediaInfo?,
    currentLyric: String?,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isExpanded) Modifier.height(140.dp)
                else Modifier.height(56.dp)
            )
            .combinedClickable(
                onClick = onExpandToggle,
                onLongClick = onLongClick
            ),
        shape = if (isExpanded) RoundedCornerShape(24.dp) else RoundedCornerShape(28.dp),
        color = IslandBackground,
        tonalElevation = 4.dp
    ) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "island_expand"
        ) { expanded ->
            if (expanded) {
                ExpandedIslandContent(
                    mediaInfo = mediaInfo,
                    currentLyric = currentLyric,
                    onPlayPause = onPlayPause,
                    onSkipNext = onSkipNext,
                    onSkipPrevious = onSkipPrevious
                )
            } else {
                CompactIslandContent(
                    mediaInfo = mediaInfo,
                    currentLyric = currentLyric,
                    onPlayPause = onPlayPause
                )
            }
        }
    }
}

@Composable
private fun CompactIslandContent(
    mediaInfo: MediaSessionBridge.MediaInfo?,
    currentLyric: String?,
    onPlayPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 小封面
        mediaInfo?.coverBitmap?.let { bitmap ->
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "封面",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                Text("♪", color = Color.Gray, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 歌名 + 当前歌词
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${mediaInfo?.displayTitle} - ${mediaInfo?.displayArtist}",
                color = PrimaryText,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            currentLyric?.let {
                Text(
                    text = it,
                    color = SecondaryText,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 播放/暂停
        IconButton(onClick = onPlayPause, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = if (mediaInfo?.isPlaying == true)
                    Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = "播放/暂停",
                tint = PrimaryText,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun ExpandedIslandContent(
    mediaInfo: MediaSessionBridge.MediaInfo?,
    currentLyric: String?,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 封面
        mediaInfo?.coverBitmap?.let { bitmap ->
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "封面",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 歌词区域
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${mediaInfo?.displayTitle} - ${mediaInfo?.displayArtist}",
                color = PrimaryText,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            currentLyric?.let {
                Text(
                    text = it,
                    color = SecondaryText,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            } ?: Text(
                text = "暂无歌词",
                color = DimText,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 进度条
            LinearProgressIndicator(
                progress = { mediaInfo?.progress ?: 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp)),
                color = ProgressActive,
                trackColor = ProgressInactive
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 控制按钮
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onSkipPrevious, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.SkipPrevious, "上一首", tint = PrimaryText, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onPlayPause, modifier = Modifier.size(36.dp)) {
                Icon(
                    if (mediaInfo?.isPlaying == true) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    "播放/暂停", tint = PrimaryText, modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onSkipNext, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Filled.SkipNext, "下一首", tint = PrimaryText, modifier = Modifier.size(20.dp))
            }
        }
    }
}
