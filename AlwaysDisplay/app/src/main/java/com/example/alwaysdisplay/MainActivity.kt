package com.example.alwaysdisplay

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.alwaysdisplay.ui.navigation.AppNavigation
import com.example.alwaysdisplay.ui.theme.AlwaysDisplayTheme
import com.example.alwaysdisplay.viewmodel.ClockViewModel
import com.example.alwaysdisplay.viewmodel.MusicViewModel

class MainActivity : ComponentActivity() {

    private lateinit var clockViewModel: ClockViewModel
    private lateinit var musicViewModel: MusicViewModel

    var isScreenAlwaysOn by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        clockViewModel = ClockViewModel()
        musicViewModel = ViewModelProvider(
            this,
            MusicViewModel.Factory(application)
        )[MusicViewModel::class.java]

        // 屏幕常亮
        updateScreenOnFlag()

        setContent {
            AlwaysDisplayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        clockViewModel = clockViewModel,
                        musicViewModel = musicViewModel
                    )
                }
            }
        }
    }

    fun setScreenAlwaysOn(enabled: Boolean) {
        isScreenAlwaysOn = enabled
        updateScreenOnFlag()
    }

    private fun updateScreenOnFlag() {
        if (isScreenAlwaysOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
