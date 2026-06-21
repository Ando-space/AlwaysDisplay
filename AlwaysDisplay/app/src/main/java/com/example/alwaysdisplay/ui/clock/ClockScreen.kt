package com.example.alwaysdisplay.ui.clock

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysdisplay.ui.theme.PrimaryText
import com.example.alwaysdisplay.ui.theme.SecondaryText
import com.example.alwaysdisplay.ui.theme.DimText

/**
 * 时钟页面 - 大字体时间+日期显示
 */
@Composable
fun ClockScreen(
    timeText: String,
    dateText: String,
    onSwipeLeft: () -> Unit = {},  // → 倒计时
    onSwipeUp: () -> Unit = {},    // → 音乐
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -100f) onSwipeLeft()
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -100f) onSwipeUp()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 大字体时间
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayLarge,
                color = PrimaryText,
                letterSpacing = (-2).sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日期
            Text(
                text = dateText,
                style = MaterialTheme.typography.displayMedium,
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 手势提示
            Text(
                text = "← 左滑倒计时 | 上滑音乐 ↑",
                style = MaterialTheme.typography.bodySmall,
                color = DimText
            )
        }
    }
}
