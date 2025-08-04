package com.example.trustie.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

@Composable
fun ShieldIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    Canvas(
        modifier = modifier.size(48.dp)
    ) {
        val width = size.width
        val height = size.height
        
        // Shield path
        val shieldPath = Path().apply {
            moveTo(width * 0.5f, height * 0.1f)
            lineTo(width * 0.8f, height * 0.25f)
            lineTo(width * 0.8f, height * 0.6f)
            cubicTo(
                width * 0.8f, height * 0.75f,
                width * 0.65f, height * 0.85f,
                width * 0.5f, height * 0.9f
            )
            cubicTo(
                width * 0.35f, height * 0.85f,
                width * 0.2f, height * 0.75f,
                width * 0.2f, height * 0.6f
            )
            lineTo(width * 0.2f, height * 0.25f)
            close()
        }
        
        drawPath(
            path = shieldPath,
            color = color
        )
        
        // Shield border
        drawPath(
            path = shieldPath,
            color = color.copy(alpha = 0.3f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx()
            )
        )
    }
} 