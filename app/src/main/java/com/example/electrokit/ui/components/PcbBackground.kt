package com.example.electrokit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PcbBackground(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF2563EB)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pcb_flow")
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val pcbAlpha = 0.025f // 2.5% opacity for maximum text readability
        val strokeWidth = 1.5.dp.toPx()

        // Path 1: Top-Left to Center
        val path1Points = listOf(
            Offset(w * 0.05f, h * 0.15f),
            Offset(w * 0.15f, h * 0.15f),
            Offset(w * 0.25f, h * 0.25f),
            Offset(w * 0.25f, h * 0.45f),
            Offset(w * 0.4f, h * 0.55f)
        )

        // Path 2: Bottom-Right to Top-Right
        val path2Points = listOf(
            Offset(w * 0.95f, h * 0.85f),
            Offset(w * 0.8f, h * 0.85f),
            Offset(w * 0.7f, h * 0.7f),
            Offset(w * 0.7f, h * 0.3f),
            Offset(w * 0.85f, h * 0.15f)
        )

        // Draw paths
        fun drawTracePath(points: List<Offset>) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[i+1],
                    strokeWidth = strokeWidth,
                    alpha = pcbAlpha
                )
            }
            points.forEach { pt ->
                drawCircle(
                    color = color,
                    radius = 4.dp.toPx(),
                    center = pt,
                    style = Stroke(width = strokeWidth),
                    alpha = pcbAlpha
                )
            }
        }

        drawTracePath(path1Points)
        drawTracePath(path2Points)

        // Draw animated flow pulses
        fun drawAnimatedSignal(points: List<Offset>, progress: Float) {
            val totalSegments = points.size - 1
            val segmentLength = 1f / totalSegments
            val currentSegment = (progress * totalSegments).toInt().coerceIn(0, totalSegments - 1)
            val segmentProgress = (progress - (currentSegment * segmentLength)) / segmentLength
            
            val start = points[currentSegment]
            val end = points[currentSegment + 1]
            val currentOffset = Offset(
                start.x + (end.x - start.x) * segmentProgress,
                start.y + (end.y - start.y) * segmentProgress
            )

            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 5.dp.toPx(),
                center = currentOffset,
                alpha = 0.35f
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = currentOffset,
                alpha = 0.7f
            )
        }

        drawAnimatedSignal(path1Points, flowProgress)
        drawAnimatedSignal(path2Points, (flowProgress + 0.5f) % 1f)
    }
}
