package com.example.alwaysdisplay.ui.music

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysdisplay.data.LyricLine
import com.example.alwaysdisplay.ui.theme.PrimaryText
import com.example.alwaysdisplay.ui.theme.DimText

/**
 * 歌词滚动显示组件
 * 当前行居中高亮，其余行暗灰
 */
@Composable
fun LyricView(
    lyrics: List<LyricLine>,
    currentLineIndex: Int,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 居中偏移计算
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0 && currentLineIndex < lyrics.size) {
            // 粗略居中：将当前行滚到可见区域中间
            val viewportHeight = listState.layoutInfo.viewportSize.height
            val itemHeight = 48.dp.value.toInt() // 近似值
            val offset = -(viewportHeight / 2 - itemHeight / 2)
            listState.animateScrollToItem(
                index = currentLineIndex,
                scrollOffset = offset
            )
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 120.dp)
    ) {
        itemsIndexed(lyrics, key = { index, _ -> index }) { index, line ->
            val isCurrent = index == currentLineIndex
            val animColor by animateColorAsState(
                targetValue = if (isCurrent) PrimaryText else DimText,
                animationSpec = tween(300),
                label = "lyric_color_$index"
            )
            val animSize by remember {
                derivedStateOf {
                    if (isCurrent) 20f else 16f
                }
            }

            Text(
                text = line.text,
                color = animColor,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                fontSize = animSize.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
