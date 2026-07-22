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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.ui.components.PcbBackground

@Composable
fun SplashScreen() {
    var startAnimation by remember { mutableStateOf(false) }

    // Fade + Scale animation with duration of 800ms (700-900ms requirement range)
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

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Dynamic theme background
        contentAlignment = Alignment.Center
    ) {
        // PCB vector background trace layout
        PcbBackground(color = Color(0xFF2563EB))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scaleVal)
                .alpha(alphaVal)
        ) {
            // Hexagonal Vector-drawn ElectroKit logo in center
            ElectroKitLogoCanvas(
                modifier = Modifier.size(130.dp),
                color = Color(0xFF2563EB)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "ElectroKit",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold, // Poppins SemiBold
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "Offline Electronics Toolkit",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal, // Inter Regular
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ElectroKitLogoCanvas(modifier: Modifier = Modifier, color: Color = Color(0xFF2563EB)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = 4.dp.toPx()

        // 1. Draw Hexagonal Outline
        val hexPath = androidx.compose.ui.graphics.Path().apply {
            val r = w / 2f
            val cx = w / 2f
            val cy = h / 2f
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60 - 30).toDouble())
                val x = cx + r * Math.cos(angle).toFloat()
                val y = cy + r * Math.sin(angle).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path = hexPath, color = color, style = Stroke(width = strokeW))

        // 2. Draw "E" letter integrated with PCB traces
        val padding = w * 0.26f
        drawLine(color = color, start = Offset(padding, h * 0.32f), end = Offset(w - padding, h * 0.32f), strokeWidth = strokeW)
        drawLine(color = color, start = Offset(padding, h * 0.5f), end = Offset(w - padding * 1.5f, h * 0.5f), strokeWidth = strokeW)
        drawLine(color = color, start = Offset(padding, h * 0.68f), end = Offset(w - padding, h * 0.68f), strokeWidth = strokeW)
        drawLine(color = color, start = Offset(padding, h * 0.32f), end = Offset(padding, h * 0.68f), strokeWidth = strokeW)

        // 3. Draw a small lightning bolt in center right
        val boltPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.56f, h * 0.32f)
            lineTo(w * 0.42f, h * 0.56f)
            lineTo(w * 0.52f, h * 0.56f)
            lineTo(w * 0.46f, h * 0.72f)
            lineTo(w * 0.60f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.48f)
            close()
        }
        drawPath(path = boltPath, color = Color(0xFFF59E0B)) // Amber color lightning bolt
    }
}
