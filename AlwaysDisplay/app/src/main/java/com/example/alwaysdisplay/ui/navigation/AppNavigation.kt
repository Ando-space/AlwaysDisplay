package com.example.alwaysdisplay.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alwaysdisplay.ui.clock.ClockScreen
import com.example.alwaysdisplay.ui.clock.TimerScreen
import com.example.alwaysdisplay.ui.music.AtomicIsland
import com.example.alwaysdisplay.ui.music.MusicScreen
import com.example.alwaysdisplay.ui.settings.SettingsScreen
import com.example.alwaysdisplay.viewmodel.ClockViewModel
import com.example.alwaysdisplay.viewmodel.MusicViewModel
import com.example.alwaysdisplay.media.MediaListenerService

/**
 * 应用导航框架
 */
@Composable
fun AppNavigation(
    clockViewModel: ClockViewModel,
    musicViewModel: MusicViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "clock"
    ) {
        composable("clock") {
            ClockPage(
                clockViewModel = clockViewModel,
                musicViewModel = musicViewModel,
                onNavigateToTimer = { navController.navigate("timer") },
                onNavigateToMusic = { navController.navigate("music") }
            )
        }

        composable("timer") {
            TimerPage(
                clockViewModel = clockViewModel,
                musicViewModel = musicViewModel,
                onNavigateToClock = { navController.popBackStack() },
                onNavigateToMusic = { navController.navigate("music") }
            )
        }

        composable("music") {
            MusicPage(
                musicViewModel = musicViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsPage(
                clockViewModel = clockViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * 时钟页面（含原子岛）
 */
@Composable
private fun ClockPage(
    clockViewModel: ClockViewModel,
    musicViewModel: MusicViewModel,
    onNavigateToTimer: () -> Unit,
    onNavigateToMusic: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        // 原子岛（有平台音乐时显示）
        val mediaState = MediaListenerService.bridge?.mediaState?.collectAsState()?.value
        if (mediaState != null) {
            AtomicIsland(
                mediaInfo = mediaState,
                currentLyric = musicViewModel.currentLyricText,
                isExpanded = musicViewModel.isIslandExpanded,
                onExpandToggle = { musicViewModel.toggleIslandExpanded() },
                onPlayPause = { musicViewModel.togglePlayPause() },
                onSkipNext = { musicViewModel.skipToNext() },
                onSkipPrevious = { musicViewModel.skipToPrevious() },
                onLongClick = { /* 跳转源App */ },
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 时钟主体
        ClockScreen(
            timeText = clockViewModel.state.timeText,
            dateText = clockViewModel.state.dateText,
            onSwipeLeft = onNavigateToTimer,
            onSwipeUp = onNavigateToMusic,
            modifier = androidx.compose.ui.Modifier.weight(1f)
        )
    }
}

/**
 * 倒计时页面
 */
@Composable
private fun TimerPage(
    clockViewModel: ClockViewModel,
    musicViewModel: MusicViewModel,
    onNavigateToClock: () -> Unit,
    onNavigateToMusic: () -> Unit
) {
    TimerScreen(
        displayText = clockViewModel.timerDisplayText,
        progress = clockViewModel.timerProgress,
        isRunning = clockViewModel.isTimerRunning,
        isFinished = clockViewModel.isTimerFinished,
        hours = clockViewModel.timerHours,
        minutes = clockViewModel.timerMinutes,
        seconds = clockViewModel.timerSeconds,
        onHoursChange = { clockViewModel.setTimerTime(it, clockViewModel.timerMinutes, clockViewModel.timerSeconds) },
        onMinutesChange = { clockViewModel.setTimerTime(clockViewModel.timerHours, it, clockViewModel.timerSeconds) },
        onSecondsChange = { clockViewModel.setTimerTime(clockViewModel.timerHours, clockViewModel.timerMinutes, it) },
        onStart = { clockViewModel.startTimer() },
        onPause = { clockViewModel.pauseTimer() },
        onReset = { clockViewModel.resetTimer() },
        onPresetSelect = { clockViewModel.setTimerPreset(it) },
        onSwipeRight = onNavigateToClock,
        onSwipeUp = onNavigateToMusic
    )
}

/**
 * 全屏音乐页面
 */
@Composable
private fun MusicPage(
    musicViewModel: MusicViewModel,
    onNavigateBack: () -> Unit
) {
    MusicScreen(
        track = musicViewModel.currentTrack,
        lyrics = musicViewModel.lyrics,
        currentLyricIndex = musicViewModel.currentLyricIndex,
        isPlaying = musicViewModel.isPlaying,
        currentPositionMs = musicViewModel.currentPositionMs,
        durationMs = musicViewModel.durationMs,
        onPlayPause = { musicViewModel.togglePlayPause() },
        onSkipNext = { musicViewModel.skipToNext() },
        onSkipPrevious = { musicViewModel.skipToPrevious() },
        onSeekTo = { musicViewModel.seekTo(it) },
        onSwipeUp = onNavigateBack
    )
}

/**
 * 设置页面
 */
@Composable
private fun SettingsPage(
    clockViewModel: ClockViewModel,
    onNavigateBack: () -> Unit
) {
    SettingsScreen(
        isScreenAlwaysOn = true, // TODO: 从DataStore读取
        isVinylAnimation = true,
        isHighlightLyrics = true,
        is24Hour = clockViewModel.is24Hour,
        onScreenAlwaysOnChange = {},
        onVinylAnimationChange = {},
        onHighlightLyricsChange = {},
        on24HourChange = { clockViewModel.set24Hour(it) },
        onNotificationListenerClick = {
            com.example.alwaysdisplay.util.PermissionHelper.openNotificationListenerSettings(
                androidx.compose.ui.platform.LocalContext.current
            )
        }
    )
}
