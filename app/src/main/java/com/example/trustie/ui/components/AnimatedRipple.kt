package com.example.trustie.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedRipples() {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale1"
    )

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale2"
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    Canvas(modifier = Modifier.size(300.dp)) {
        val center = size.width / 2f
        val baseRadius = 60.dp.toPx()

        // First ripple
        drawCircle(
            color = Color(0xFF2196F3).copy(alpha = alpha1),
            radius = baseRadius * scale1,
            center = Offset(center, center)
        )

        // Second ripple
        drawCircle(
            color = Color(0xFF2196F3).copy(alpha = alpha2),
            radius = baseRadius * scale2,
            center = Offset(center, center)
        )
    }
}