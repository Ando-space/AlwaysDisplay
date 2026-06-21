package com.example.alwaysdisplay.media

import android.graphics.Bitmap
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.media.MediaMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * MediaSession桥接层
 * 从第三方音乐App的MediaSession中提取封面、元数据、播放状态
 * 并提供播放控制能力
 */
class MediaSessionBridge {

    private val controllers = mutableMapOf<String, MediaController>()
    private val _mediaState = MutableStateFlow<MediaInfo?>(null)
    val mediaState: StateFlow<MediaInfo?> = _mediaState.asStateFlow()

    data class MediaInfo(
        val packageName: String,
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long,
        val position: Long,
        val isPlaying: Boolean,
        val coverBitmap: Bitmap?,
        val controller: MediaController
    ) {
        val displayTitle: String get() = title.ifBlank { "未知歌曲" }
        val displayArtist: String get() = artist.ifBlank { "未知歌手" }
        val progress: Float get() = if (duration > 0) position.toFloat() / duration else 0f
    }

    fun connect(context: android.content.Context, packageName: String, token: MediaSession.Token) {
        if (controllers.containsKey(packageName)) return

        try {
            val controller = MediaController(context, token)
            controller.registerCallback(object : MediaController.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadata?) {
                    updateState(packageName, controller)
                }

                override fun onPlaybackStateChanged(state: PlaybackState?) {
                    updateState(packageName, controller)
                }
            })
            controllers[packageName] = controller
            updateState(packageName, controller)
        } catch (e: Exception) {
            // 某些App可能拒绝连接
        }
    }

    fun removeSession(packageName: String) {
        controllers.remove(packageName)?.unregisterCallback(null)
        if (_mediaState.value?.packageName == packageName) {
            _mediaState.value = null
        }
    }

    private fun updateState(packageName: String, controller: MediaController) {
        val metadata = controller.metadata ?: return
        val state = controller.playbackState ?: return

        _mediaState.value = MediaInfo(
            packageName = packageName,
            title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "",
            artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "",
            album = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: "",
            duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION),
            position = state.position,
            isPlaying = state.state == PlaybackState.STATE_PLAYING,
            coverBitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART),
            controller = controller
        )
    }

    // 播放控制
    fun play() { _mediaState.value?.controller?.transportControls?.play() }
    fun pause() { _mediaState.value?.controller?.transportControls?.pause() }
    fun skipToNext() { _mediaState.value?.controller?.transportControls?.skipToNext() }
    fun skipToPrevious() { _mediaState.value?.controller?.transportControls?.skipToPrevious() }
    fun seekTo(pos: Long) { _mediaState.value?.controller?.transportControls?.seekTo(pos) }

    fun release() {
        controllers.values.forEach { it.unregisterCallback(null) }
        controllers.clear()
        _mediaState.value = null
    }
}
