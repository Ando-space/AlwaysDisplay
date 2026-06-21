package com.example.alwaysdisplay.ui.music

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * 半圆唱片动画组件
 * 播放时顺时针旋转，暂停时保持当前角度
 */
@Composable
fun VinylDisc(
    coverBitmap: android.graphics.Bitmap?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    showHalf: Boolean = true
) {
    var currentRotation by remember { mutableFloatStateOf(0f) }
    val rotationAnim = remember { Animatable(currentRotation) }

    // 播放时持续旋转，暂停时保持当前角度
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            rotationAnim.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = value
            }
        } else {
            rotationAnim.snapTo(currentRotation)
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(if (showHalf) 2f else 1f)
            .clip(CircleShape)
    ) {
        // 唱片主体
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    rotationZ = rotationAnim.value
                }
        ) {
            // 封面图片
            coverBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "专辑封面",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // 唱片纹路叠加
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                for (i in 1..8) {
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.15f),
                        radius = size.minDimension * i / 10f,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                    )
                }
                // 中心圆
                drawCircle(
                    color = Color(0xFF333333),
                    radius = size.minDimension * 0.08f,
                    center = center
                )
                // 中心小圆
                drawCircle(
                    color = Color(0xFF1A1A1A),
                    radius = size.minDimension * 0.03f,
                    center = center
                )
            }
        }

        // 右侧遮罩（半圆效果）
        if (showHalf) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(LocalDensity.current) { (size.width / 2).toDp() })
                    .align(Alignment.CenterEnd)
                    .graphicsLayer {
                        translationX = 0f
                    }
                    .offset(x = with(LocalDensity.current) { (size.width * 0.05f).toDp() })
            )
        }
    }
}
