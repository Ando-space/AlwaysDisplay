package com.example.alwaysdisplay.ui.clock

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysdisplay.ui.theme.PrimaryText
import com.example.alwaysdisplay.ui.theme.SecondaryText
import com.example.alwaysdisplay.ui.theme.DimText
import com.example.alwaysdisplay.ui.theme.CardBackground
import com.example.alwaysdisplay.ui.theme.ProgressActive
import com.example.alwaysdisplay.ui.theme.ProgressInactive

/**
 * 倒计时页面
 */
@Composable
fun TimerScreen(
    displayText: String,
    progress: Float,
    isRunning: Boolean,
    isFinished: Boolean,
    hours: Int,
    minutes: Int,
    seconds: Int,
    onHoursChange: (Int) -> Unit = {},
    onMinutesChange: (Int) -> Unit = {},
    onSecondsChange: (Int) -> Unit = {},
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onReset: () -> Unit = {},
    onPresetSelect: (Int) -> Unit = {},
    onSwipeRight: () -> Unit = {},  // → 时钟
    onSwipeUp: () -> Unit = {},     // → 音乐
    modifier: Modifier = Modifier
) {
    val presets = listOf(1 to "1m", 5 to "5m", 10 to "10m", 25 to "25m")

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount > 100f) onSwipeRight()
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -100f) onSwipeUp()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 时间选择器（未运行时显示）
            if (!isRunning && !isFinished) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimeWheel(
                        value = hours,
                        range = 0..23,
                        label = "时",
                        onValueChange = onHoursChange
                    )
                    Text(":", color = PrimaryText, fontSize = 32.sp)
                    TimeWheel(
                        value = minutes,
                        range = 0..59,
                        label = "分",
                        onValueChange = onMinutesChange
                    )
                    Text(":", color = PrimaryText, fontSize = 32.sp)
                    TimeWheel(
                        value = seconds,
                        range = 0..59,
                        label = "秒",
                        onValueChange = onSecondsChange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 快捷预设
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presets) { (mins, label) ->
                        OutlinedButton(
                            onClick = { onPresetSelect(mins) },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SecondaryText
                            )
                        ) {
                            Text(label, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 倒计时显示
            Text(
                text = displayText,
                style = MaterialTheme.typography.displayLarge,
                color = if (isFinished) Color(0xFFFF4444) else PrimaryText
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 进度条
            if (isRunning || isFinished) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(4.dp),
                    color = if (isFinished) Color(0xFFFF4444) else ProgressActive,
                    trackColor = ProgressInactive
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 控制按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (!isRunning && !isFinished) {
                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryText,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("开始", fontSize = 16.sp)
                    }
                } else if (isRunning) {
                    OutlinedButton(
                        onClick = onPause,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText)
                    ) {
                        Text("暂停", fontSize = 16.sp)
                    }
                }

                if (isRunning || isFinished) {
                    OutlinedButton(
                        onClick = onReset,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DimText)
                    ) {
                        Text("重置", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 手势提示
            Text(
                text = "→ 右滑时钟 | 上滑音乐 ↑",
                style = MaterialTheme.typography.bodySmall,
                color = DimText
            )
        }
    }
}

@Composable
private fun TimeWheel(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 简化版：用按钮增减
        TextButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Text("▲", color = DimText, fontSize = 16.sp)
        }
        Text(
            text = String.format("%02d", value),
            color = PrimaryText,
            fontSize = 36.sp
        )
        TextButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Text("▼", color = DimText, fontSize = 16.sp)
        }
        Text(label, color = DimText, fontSize = 12.sp)
    }
}
