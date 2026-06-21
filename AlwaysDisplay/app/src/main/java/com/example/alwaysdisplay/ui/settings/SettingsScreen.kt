package com.example.alwaysdisplay.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alwaysdisplay.ui.theme.PrimaryText
import com.example.alwaysdisplay.ui.theme.SecondaryText
import com.example.alwaysdisplay.ui.theme.DimText
import com.example.alwaysdisplay.ui.theme.CardBackground
import com.example.alwaysdisplay.ui.theme.Divider

/**
 * 设置页面
 */
@Composable
fun SettingsScreen(
    isScreenAlwaysOn: Boolean,
    isVinylAnimation: Boolean,
    isHighlightLyrics: Boolean,
    is24Hour: Boolean,
    onScreenAlwaysOnChange: (Boolean) -> Unit,
    onVinylAnimationChange: (Boolean) -> Unit,
    onHighlightLyricsChange: (Boolean) -> Unit,
    on24HourChange: (Boolean) -> Unit,
    onNotificationListenerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 显示设置
        SettingSectionTitle("显示")
        SettingSwitch(
            title = "屏幕常亮",
            subtitle = "防止屏幕自动息屏",
            checked = isScreenAlwaysOn,
            onCheckedChange = onScreenAlwaysOnChange
        )
        SettingSwitch(
            title = "24小时制",
            subtitle = "使用24小时格式显示时间",
            checked = is24Hour,
            onCheckedChange = on24HourChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 音乐设置
        SettingSectionTitle("音乐")
        SettingSwitch(
            title = "唱片动画",
            subtitle = "播放时显示旋转唱片",
            checked = isVinylAnimation,
            onCheckedChange = onVinylAnimationChange
        )
        SettingSwitch(
            title = "高亮歌词",
            subtitle = "当前歌词行高亮显示",
            checked = isHighlightLyrics,
            onCheckedChange = onHighlightLyricsChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 权限设置
        SettingSectionTitle("权限")
        SettingAction(
            title = "通知监听权限",
            subtitle = "开启后可获取平台音乐信息",
            onClick = onNotificationListenerClick
        )
    }
}

@Composable
private fun SettingSectionTitle(title: String) {
    Text(
        text = title,
        color = SecondaryText,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        color = CardBackground,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = PrimaryText)
                Text(subtitle, color = DimText, style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = PrimaryText,
                    checkedThumbColor = CardBackground
                )
            )
        }
    }
}

@Composable
private fun SettingAction(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        color = CardBackground,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = PrimaryText)
                Text(subtitle, color = DimText, style = MaterialTheme.typography.bodySmall)
            }
            Text("→", color = DimText)
        }
    }
}
