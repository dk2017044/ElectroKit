package com.example.electrokit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pcb_flow")
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_progress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val pcbAlpha = 0.06f // Soft, subtle 6% circuit trace opacity
        val strokeWidth = 1.4.dp.toPx()

        // Path 1: Top-Left to Center-Right
        val path1Points = listOf(
            Offset(w * 0.05f, h * 0.12f),
            Offset(w * 0.20f, h * 0.12f),
            Offset(w * 0.35f, h * 0.25f),
            Offset(w * 0.35f, h * 0.48f),
            Offset(w * 0.55f, h * 0.62f),
            Offset(w * 0.85f, h * 0.62f)
        )

        // Path 2: Bottom-Right to Top-Right
        val path2Points = listOf(
            Offset(w * 0.95f, h * 0.88f),
            Offset(w * 0.75f, h * 0.88f),
            Offset(w * 0.60f, h * 0.75f),
            Offset(w * 0.60f, h * 0.35f),
            Offset(w * 0.85f, h * 0.18f)
        )

        // Path 3: Bottom-Left to Center-Top
        val path3Points = listOf(
            Offset(w * 0.08f, h * 0.82f),
            Offset(w * 0.28f, h * 0.82f),
            Offset(w * 0.45f, h * 0.68f),
            Offset(w * 0.45f, h * 0.38f)
        )

        // Path 4: Mid-Left to Bottom-Middle
        val path4Points = listOf(
            Offset(w * 0.02f, h * 0.45f),
            Offset(w * 0.18f, h * 0.45f),
            Offset(w * 0.32f, h * 0.58f),
            Offset(w * 0.32f, h * 0.90f)
        )

        // Path 5: Top-Right to Center-Left
        val path5Points = listOf(
            Offset(w * 0.88f, h * 0.05f),
            Offset(w * 0.88f, h * 0.22f),
            Offset(w * 0.68f, h * 0.42f),
            Offset(w * 0.25f, h * 0.42f)
        )

        // Draw static circuit trace lines & PCB pad nodes
        fun drawTracePath(points: List<Offset>) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = strokeWidth,
                    alpha = pcbAlpha
                )
            }
            points.forEach { pt ->
                drawCircle(
                    color = color,
                    radius = 3.0.dp.toPx(),
                    center = pt,
                    style = Stroke(width = strokeWidth),
                    alpha = pcbAlpha * 1.2f
                )
            }
        }

        drawTracePath(path1Points)
        drawTracePath(path2Points)
        drawTracePath(path3Points)
        drawTracePath(path4Points)
        drawTracePath(path5Points)

        // Draw subtle, soft, calm animated signal pulses traveling along circuit paths
        fun drawAnimatedSignal(points: List<Offset>, progress: Float, signalColor: Color) {
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

            // Outer Soft Energy Aura
            drawCircle(
                color = signalColor.copy(alpha = 0.15f),
                radius = 8.dp.toPx(),
                center = currentOffset
            )

            // Inner Soft Signal Node
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.35f),
                radius = 3.dp.toPx(),
                center = currentOffset
            )

            // Subtle Core Spark Center
            drawCircle(
                color = Color.White.copy(alpha = 0.60f),
                radius = 1.2.dp.toPx(),
                center = currentOffset
            )
        }

        drawAnimatedSignal(path1Points, flowProgress, color)
        drawAnimatedSignal(path2Points, (flowProgress + 0.25f) % 1f, Color(0xFF00E5FF))
        drawAnimatedSignal(path3Points, (flowProgress + 0.50f) % 1f, color)
        drawAnimatedSignal(path4Points, (flowProgress + 0.75f) % 1f, Color(0xFF00E5FF))
        drawAnimatedSignal(path5Points, (flowProgress + 0.15f) % 1f, color)
    }
}
