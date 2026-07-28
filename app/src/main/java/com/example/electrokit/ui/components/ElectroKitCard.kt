package com.example.electrokit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ElectroKitCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 4.dp,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Continuous, seamless infinite shimmer sweep loop that NEVER turns dark or off
    val shimmerTransition = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerShift by shimmerTransition.animateFloat(
        initialValue = -400f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_shift"
    )

    Card(
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = accentColor.copy(alpha = 0.16f),
                spotColor = accentColor.copy(alpha = 0.22f)
            )
            .border(
                BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
                RoundedCornerShape(cornerRadius)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            surfaceColor.copy(alpha = 0.95f),
                            accentColor.copy(alpha = 0.12f),
                            accentColor.copy(alpha = 0.24f),
                            accentColor.copy(alpha = 0.12f),
                            surfaceColor.copy(alpha = 0.95f)
                        ),
                        start = Offset(shimmerShift - 400f, shimmerShift - 400f),
                        end = Offset(shimmerShift, shimmerShift)
                    )
                )
        ) {
            content()
        }
    }
}
