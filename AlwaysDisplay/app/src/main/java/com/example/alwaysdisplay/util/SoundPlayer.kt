package com.example.alwaysdisplay.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.alwaysdisplay.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 倒计时提示音播放器（使用SoundPool，适合短音效）
 */
class SoundPlayer(private val context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var beepSoundId: Int = 0
    private var isLoaded = false

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            isLoaded = status == 0
        }
        try {
            beepSoundId = soundPool.load(context, R.raw.beep, 1)
        } catch (e: Exception) {
            // beep.mp3可能不存在，使用备用方案
            isLoaded = false
        }
    }

    fun playBeep() {
        if (isLoaded) {
            soundPool.play(beepSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playBeepRepeat(times: Int = 3, intervalMs: Long = 500) {
        if (!isLoaded) return
        CoroutineScope(Dispatchers.Main).launch {
            repeat(times) {
                soundPool.play(beepSoundId, 1f, 1f, 1, 0, 1f)
                delay(intervalMs)
            }
        }
    }

    fun release() {
        soundPool.release()
    }
}
