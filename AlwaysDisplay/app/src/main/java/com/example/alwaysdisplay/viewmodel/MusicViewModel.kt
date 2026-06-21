package com.example.alwaysdisplay.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alwaysdisplay.data.Lyrics
import com.example.alwaysdisplay.data.LyricLine
import com.example.alwaysdisplay.data.LyricRepository
import com.example.alwaysdisplay.data.MusicScanner
import com.example.alwaysdisplay.data.MusicTrack
import com.example.alwaysdisplay.media.MediaListenerService
import com.example.alwaysdisplay.media.MediaSessionBridge
import com.example.alwaysdisplay.network.LyricApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MusicDisplayMode {
    ATOMIC_ISLAND,  // 原子岛紧凑模式
    FULL_SCREEN     // 全屏歌词模式
}

enum class PlayMode {
    LOOP_ALL,       // 列表循环
    LOOP_ONE,       // 单曲循环
    SHUFFLE         // 随机播放
}

class MusicViewModel(application: Application) : ViewModel() {

    var currentTrack by mutableStateOf<MusicTrack?>(null)
        private set

    var lyrics by mutableStateOf(Lyrics.Empty)
        private set

    var currentLyricIndex by mutableIntStateOf(-1)
        private set

    var currentLyricText by mutableStateOf<String?>(null)
        private set

    var displayMode by mutableStateOf(MusicDisplayMode.ATOMIC_ISLAND)
        private set

    var playMode by mutableStateOf(PlayMode.LOOP_ALL)
        private set

    var isIslandExpanded by mutableStateOf(false)
        private set

    // 本地音乐列表
    var localTracks by mutableStateOf<List<MusicScanner.LocalTrack>>(emptyList())
        private set

    var isLocalMode by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var playbackPosition by mutableFloatStateOf(0f)
        private set

    var currentPositionMs by mutableLongStateOf(0L)
        private set

    var durationMs by mutableLongStateOf(0L)
        private set

    private val lyricRepository = LyricRepository(LyricApiService(), application)
    private var lyricJob: Job? = null
    private var progressJob: Job? = null

    init {
        observePlatformMedia()
        loadLocalMusic(application)
    }

    private fun observePlatformMedia() {
        viewModelScope.launch {
            MediaListenerService.bridge?.mediaState?.collect { info ->
                if (info != null) {
                    currentTrack = MusicTrack(
                        id = info.packageName,
                        title = info.title,
                        artist = info.artist,
                        album = info.album,
                        duration = info.duration,
                        position = info.position,
                        isPlaying = info.isPlaying,
                        coverBitmap = info.coverBitmap,
                        source = MusicTrack.Source.PLATFORM,
                        packageName = info.packageName
                    )
                    isPlaying = info.isPlaying
                    durationMs = info.duration
                    currentPositionMs = info.position
                    isLocalMode = false

                    // 获取歌词
                    fetchLyrics(info.title, info.artist)
                    startProgressTracking()
                } else {
                    // 平台音乐停止，不立即清空（可能是短暂断开）
                }
            }
        }
    }

    private fun loadLocalMusic(application: Application) {
        viewModelScope.launch {
            localTracks = MusicScanner.scan(application)
        }
    }

    private fun fetchLyrics(title: String, artist: String) {
        lyricJob?.cancel()
        lyricJob = viewModelScope.launch {
            lyrics = lyricRepository.getLyrics(title, artist)
        }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                val track = currentTrack ?: break
                if (track.source == MusicTrack.Source.PLATFORM) {
                    // 从MediaSession获取实时位置
                    val bridge = MediaListenerService.bridge
                    val state = bridge?.mediaState?.value
                    if (state != null) {
                        currentPositionMs = state.position
                        playbackPosition = state.progress
                        isPlaying = state.isPlaying
                    }
                }
                // 更新歌词行
                updateCurrentLyric()
                delay(200)
            }
        }
    }

    private fun updateCurrentLyric() {
        if (lyrics.lines.isEmpty()) {
            currentLyricIndex = -1
            currentLyricText = null
            return
        }
        val idx = lyrics.getLineIndexAt(currentPositionMs)
        if (idx != currentLyricIndex) {
            currentLyricIndex = idx
            currentLyricText = if (idx >= 0) lyrics.lines[idx].text else null
        }
    }

    // 播放控制（平台音乐）
    fun play() {
        MediaListenerService.bridge?.play()
    }

    fun pause() {
        MediaListenerService.bridge?.pause()
    }

    fun togglePlayPause() {
        if (isPlaying) pause() else play()
    }

    fun skipToNext() {
        MediaListenerService.bridge?.skipToNext()
    }

    fun skipToPrevious() {
        MediaListenerService.bridge?.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        MediaListenerService.bridge?.seekTo(positionMs)
    }

    // UI控制
    fun toggleDisplayMode() {
        displayMode = if (displayMode == MusicDisplayMode.ATOMIC_ISLAND)
            MusicDisplayMode.FULL_SCREEN
        else
            MusicDisplayMode.ATOMIC_ISLAND
    }

    fun setDisplayMode(mode: MusicDisplayMode) {
        displayMode = mode
    }

    fun toggleIslandExpanded() {
        isIslandExpanded = !isIslandExpanded
    }

    fun togglePlayMode() {
        playMode = when (playMode) {
            PlayMode.LOOP_ALL -> PlayMode.LOOP_ONE
            PlayMode.LOOP_ONE -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.LOOP_ALL
        }
    }

    override fun onCleared() {
        super.onCleared()
        lyricJob?.cancel()
        progressJob?.cancel()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MusicViewModel(application) as T
        }
    }
}
