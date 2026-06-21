package com.example.alwaysdisplay.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ClockState(
    val timeText: String = "00:00:00",
    val dateText: String = "",
    val is24Hour: Boolean = true
)

class ClockViewModel : ViewModel() {

    var state by mutableStateOf(ClockState())
        private set

    var is24Hour by mutableStateOf(true)
        private set

    // 倒计时相关
    var timerHours by mutableIntStateOf(0)
    var timerMinutes by mutableIntStateOf(5)
    var timerSeconds by mutableIntStateOf(0)

    var timerRemainingMs by mutableLongStateOf(0L)
    var isTimerRunning by mutableStateOf(false)
    var isTimerFinished by mutableStateOf(false)

    private var timerJob: Job? = null
    private var timerTotalMs: Long = 0L

    init {
        startClock()
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                val now = Calendar.getInstance()
                val timeFormat = if (is24Hour) "HH:mm:ss" else "hh:mm:ss a"
                state = state.copy(
                    timeText = SimpleDateFormat(timeFormat, Locale.getDefault()).format(now.time),
                    dateText = formatDate(now)
                )
                // 对齐秒边界，避免漂移
                val delayMs = 1000L - (System.currentTimeMillis() % 1000)
                delay(delayMs)
            }
        }
    }

    private fun formatDate(calendar: Calendar): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val weekDays = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val weekDay = weekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        return "${year}年${month}月${day}日 $weekDay"
    }

    fun set24Hour(enabled: Boolean) {
        is24Hour = enabled
        state = state.copy(is24Hour = enabled)
    }

    // 倒计时控制
    fun setTimerPreset(minutes: Int) {
        timerHours = minutes / 60
        timerMinutes = minutes % 60
        timerSeconds = 0
        timerTotalMs = minutes * 60_000L
        timerRemainingMs = timerTotalMs
    }

    fun setTimerTime(hours: Int, minutes: Int, seconds: Int) {
        timerHours = hours
        timerMinutes = minutes
        timerSeconds = seconds
        timerTotalMs = (hours * 3600 + minutes * 60 + seconds) * 1000L
        timerRemainingMs = timerTotalMs
    }

    fun startTimer() {
        if (isTimerRunning) return
        if (timerRemainingMs <= 0L) {
            timerRemainingMs = timerTotalMs
        }
        isTimerRunning = true
        isTimerFinished = false

        timerJob = viewModelScope.launch {
            while (timerRemainingMs > 0 && isTimerRunning) {
                delay(100L)
                timerRemainingMs -= 100L
                if (timerRemainingMs < 0) timerRemainingMs = 0
            }
            if (timerRemainingMs <= 0) {
                isTimerRunning = false
                isTimerFinished = true
            }
        }
    }

    fun pauseTimer() {
        isTimerRunning = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        isTimerRunning = false
        timerJob?.cancel()
        timerRemainingMs = timerTotalMs
        isTimerFinished = false
    }

    val timerDisplayText: String
        get() {
            val total = timerRemainingMs / 1000
            val h = total / 3600
            val m = (total % 3600) / 60
            val s = total % 60
            return if (h > 0) String.format("%02d:%02d:%02d", h, m, s)
            else String.format("%02d:%02d", m, s)
        }

    val timerProgress: Float
        get() = if (timerTotalMs > 0) 1f - (timerRemainingMs.toFloat() / timerTotalMs) else 0f
}
