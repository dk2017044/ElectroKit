package com.example.electrokit.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.ui.components.PcbBackground
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen() {
    var startAnimation by remember { mutableStateOf(false) }

    // Fade + Scale animation for entry
    val scaleVal by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.5f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "splash_scale"
    )

    val alphaVal by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "splash_alpha"
    )

    // Breathing Electric Energy Radial Aura Glow behind Logo
    val auraTransition = rememberInfiniteTransition(label = "electric_aura")
    val auraAlpha by auraTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.70f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // Lightning Bolt Intensity & Pulse Oscillation (1.2-1.5s cycle)
    val boltPulse by auraTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bolt_pulse"
    )

    // Radiating Electric Sparks Cycle Loop (2.4s continuous loop)
    val sparkCycle by auraTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spark_cycle"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val primaryAccent = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // PCB vector background pattern
        PcbBackground(color = primaryAccent)

        // Dark background soft radial glow adapting to user's accent color
        Canvas(modifier = Modifier.size(260.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryAccent.copy(alpha = auraAlpha * 0.50f),
                        Color(0xFF00E5FF).copy(alpha = auraAlpha * 0.20f),
                        Color.Transparent
                    )
                ),
                radius = size.width / 2f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleVal)
                .alpha(alphaVal)
        ) {
            // Hexagonal Vector Logo with integrated E & K, Orange Bolt, and Radiating Electric Sparks
            ElectroKitLogoCanvas(
                modifier = Modifier.size(145.dp),
                accentColor = primaryAccent,
                boltPulse = boltPulse,
                sparkCycle = sparkCycle
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "ElectroKit",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(primaryAccent, androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Offline Electronics Toolkit",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
fun ElectroKitLogoCanvas(
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    boltPulse: Float = 1.0f,
    sparkCycle: Float = 0f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 3.5.dp.toPx()
        val cx = w / 2f
        val cy = h / 2f

        // 1. Draw Hexagonal Outline adapting to Accent Color
        val hexPath = androidx.compose.ui.graphics.Path().apply {
            val r = w / 2f
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60 - 30).toDouble())
                val x = cx + r * cos(angle).toFloat()
                val y = cy + r * sin(angle).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path = hexPath, color = accentColor, style = Stroke(width = strokeW))

        // 2. Draw Integrated "E" & "K" Circuit Letters (Adapts to Accent Color)
        // ── Integrated "E" Letter on Left Side ─────────────────────────────────
        val eX = w * 0.24f
        val eTopY = h * 0.30f
        val eMidY = h * 0.50f
        val eBotY = h * 0.70f

        // "E" Spine
        drawLine(color = accentColor, start = Offset(eX, eTopY), end = Offset(eX, eBotY), strokeWidth = strokeW)
        // "E" Arms
        drawLine(color = accentColor, start = Offset(eX, eTopY), end = Offset(cx - 10.dp.toPx(), eTopY), strokeWidth = strokeW)
        drawLine(color = accentColor, start = Offset(eX, eMidY), end = Offset(cx - 14.dp.toPx(), eMidY), strokeWidth = strokeW)
        drawLine(color = accentColor, start = Offset(eX, eBotY), end = Offset(cx - 10.dp.toPx(), eBotY), strokeWidth = strokeW)

        // PCB Nodes for "E"
        drawCircle(color = accentColor, radius = 2.5.dp.toPx(), center = Offset(cx - 10.dp.toPx(), eTopY))
        drawCircle(color = accentColor, radius = 2.5.dp.toPx(), center = Offset(cx - 14.dp.toPx(), eMidY))
        drawCircle(color = accentColor, radius = 2.5.dp.toPx(), center = Offset(cx - 10.dp.toPx(), eBotY))

        // ── Integrated "K" Letter on Right Side ────────────────────────────────
        val kX = w * 0.76f
        val kTopY = h * 0.30f
        val kBotY = h * 0.70f

        // "K" Spine
        drawLine(color = accentColor, start = Offset(kX, kTopY), end = Offset(kX, kBotY), strokeWidth = strokeW)
        // "K" Diagonals
        drawLine(color = accentColor, start = Offset(kX, eMidY), end = Offset(cx + 10.dp.toPx(), kTopY), strokeWidth = strokeW)
        drawLine(color = accentColor, start = Offset(kX, eMidY), end = Offset(cx + 10.dp.toPx(), kBotY), strokeWidth = strokeW)

        // PCB Nodes for "K"
        drawCircle(color = accentColor, radius = 2.5.dp.toPx(), center = Offset(cx + 10.dp.toPx(), kTopY))
        drawCircle(color = accentColor, radius = 2.5.dp.toPx(), center = Offset(cx + 10.dp.toPx(), kBotY))

        // 3. Dynamic Radiating Electric Sparks & Current Flow Lines Outward
        val sparkAngles = listOf(0.0, 45.0, 90.0, 135.0, 180.0, 225.0, 270.0, 315.0)
        sparkAngles.forEachIndexed { idx, angleDeg ->
            val phaseShift = (idx * 0.125f + sparkCycle) % 1.0f
            val distance = (w * 0.15f) + phaseShift * (w * 0.38f)
            val fadeAlpha = (1f - phaseShift) * 0.85f

            val angleRad = Math.toRadians(angleDeg)
            val sparkX = cx + distance * cos(angleRad).toFloat()
            val sparkY = cy + distance * sin(angleRad).toFloat()

            // Outer Spark Point Particle (Orange / Accent Blend)
            val sparkColor = if (idx % 2 == 0) Color(0xFFF59E0B) else accentColor
            drawCircle(
                color = sparkColor.copy(alpha = fadeAlpha),
                radius = (2.5.dp.toPx() * (1f - phaseShift * 0.5f)),
                center = Offset(sparkX, sparkY)
            )

            // Dynamic Electric Flow Ray
            val rayStartDist = distance - (12.dp.toPx() * (1f - phaseShift))
            val rayStartX = cx + rayStartDist * cos(angleRad).toFloat()
            val rayStartY = cy + rayStartDist * sin(angleRad).toFloat()

            drawLine(
                color = Color.White.copy(alpha = fadeAlpha * 0.7f),
                start = Offset(rayStartX, rayStartY),
                end = Offset(sparkX, sparkY),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // 4. Central Orange Lightning Bolt (Energy Identity)
        val orangeBoltColor = Color(0xFFF59E0B)
        val yellowBoltColor = Color(0xFFFFD166)

        val boltPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + 6.dp.toPx(), cy - 26.dp.toPx())
            lineTo(cx - 12.dp.toPx(), cy + 2.dp.toPx())
            lineTo(cx + 1.dp.toPx(), cy + 2.dp.toPx())
            lineTo(cx - 7.dp.toPx(), cy + 24.dp.toPx())
            lineTo(cx + 12.dp.toPx(), cy - 6.dp.toPx())
            lineTo(cx - 1.dp.toPx(), cy - 6.dp.toPx())
            close()
        }

        // 4a. Outer Aura Glow around Lightning Bolt blending Orange with user's Accent Color
        drawPath(
            path = boltPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    orangeBoltColor.copy(alpha = 0.6f * boltPulse),
                    accentColor.copy(alpha = 0.4f * boltPulse),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = w * 0.3f
            ),
            style = Stroke(width = (8.dp.toPx() * boltPulse))
        )

        // 4b. Core Orange & Yellow Gradient Energy Lightning Bolt
        drawPath(
            path = boltPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    yellowBoltColor,
                    orangeBoltColor
                )
            )
        )

        // 4c. Inner Electric White Spark Highlight inside Lightning Bolt
        val innerSparkPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + 4.dp.toPx(), cy - 20.dp.toPx())
            lineTo(cx - 8.dp.toPx(), cy + 2.dp.toPx())
            lineTo(cx - 2.dp.toPx(), cy + 2.dp.toPx())
            lineTo(cx - 5.dp.toPx(), cy + 18.dp.toPx())
        }
        drawPath(
            path = innerSparkPath,
            color = Color.White.copy(alpha = 0.85f * boltPulse),
            style = Stroke(width = 1.8.dp.toPx())
        )
    }
}
