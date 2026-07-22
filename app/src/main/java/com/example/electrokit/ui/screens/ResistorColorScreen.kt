package com.example.electrokit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.ResistorColor
import com.example.electrokit.domain.calculations.ResistorColorCode
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResistorColorScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    var b1 by remember { mutableStateOf(ResistorColor.BROWN) }
    var b2 by remember { mutableStateOf(ResistorColor.BLACK) }
    var mult by remember { mutableStateOf(ResistorColor.RED) }
    var tol by remember { mutableStateOf(ResistorColor.GOLD) }
    var isVisualMode by remember { mutableStateOf(false) }

    val calcResult = remember(b1, b2, mult, tol) {
        ResistorColorCode.decode4Band(b1, b2, mult, tol)
    }

    var triggerShimmer by remember { mutableStateOf(0) }
    LaunchedEffect(b1, b2, mult, tol) {
        triggerShimmer = 1
        kotlinx.coroutines.delay(1000)
        triggerShimmer = 0
    }
    
    val shimmerAlpha by animateFloatAsState(
        targetValue = if (triggerShimmer > 0) 0.6f else 0.0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "band_shimmer"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resistor Color Code (4-Band)", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(2.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PcbBackground(color = Color(0xFF2563EB))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFD2B48C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(vertical = 8.dp)
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Band 1
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight()
                                .background(parseColor(b1.hex))
                        ) {
                            if (shimmerAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = shimmerAlpha))
                                )
                            }
                        }

                        // Band 2
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight()
                                .background(parseColor(b2.hex))
                        ) {
                            if (shimmerAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = shimmerAlpha))
                                )
                            }
                        }

                        // Multiplier Band
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight()
                                .background(parseColor(mult.hex))
                        ) {
                            if (shimmerAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = shimmerAlpha))
                                )
                            }
                        }

                        // Tolerance Band
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .fillMaxHeight()
                                .background(parseColor(tol.hex))
                        ) {
                            if (shimmerAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = shimmerAlpha))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(false to "Standard Mode", true to "Visual Mode").forEach { (mode, label) ->
                        val selected = isVisualMode == mode
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .background(
                                    color = if (selected) Color(0xFF2563EB) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { isVisualMode = mode },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isVisualMode) {
                    // Band 1 Selector
                    Text("Band 1 (1st Digit)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2563EB))
                    ColorDropdown(selected = b1, onSelect = { b1 = it }, options = listOf(
                        ResistorColor.BLACK, ResistorColor.BROWN, ResistorColor.RED, ResistorColor.ORANGE,
                        ResistorColor.YELLOW, ResistorColor.GREEN, ResistorColor.BLUE,
                        ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.WHITE
                    ))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Band 2 Selector
                    Text("Band 2 (2nd Digit)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2563EB))
                    ColorDropdown(selected = b2, onSelect = { b2 = it }, options = listOf(
                        ResistorColor.BLACK, ResistorColor.BROWN, ResistorColor.RED,
                        ResistorColor.ORANGE, ResistorColor.YELLOW, ResistorColor.GREEN,
                        ResistorColor.BLUE, ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.WHITE
                    ))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multiplier Selector
                    Text("Multiplier", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2563EB))
                    ColorDropdown(selected = mult, onSelect = { mult = it }, options = listOf(
                        ResistorColor.BLACK, ResistorColor.BROWN, ResistorColor.RED,
                        ResistorColor.ORANGE, ResistorColor.YELLOW, ResistorColor.GREEN,
                        ResistorColor.BLUE, ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.WHITE,
                        ResistorColor.GOLD, ResistorColor.SILVER
                    ))

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tolerance Selector
                    Text("Tolerance", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2563EB))
                    ColorDropdown(selected = tol, onSelect = { tol = it }, options = listOf(
                        ResistorColor.BROWN, ResistorColor.RED, ResistorColor.ORANGE,
                        ResistorColor.YELLOW, ResistorColor.GREEN, ResistorColor.BLUE,
                        ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.GOLD, ResistorColor.SILVER
                    ))
                } else {
                    // Visual radio grid layout matching the user's screenshots
                    VisualColorSelector(
                        title = "1st Band Color:",
                        selected = b1,
                        options = listOf(
                            ResistorColor.BLACK, ResistorColor.BROWN, ResistorColor.RED,
                            ResistorColor.ORANGE, ResistorColor.YELLOW, ResistorColor.GREEN,
                            ResistorColor.BLUE, ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.WHITE
                        ),
                        onSelect = { b1 = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    VisualColorSelector(
                        title = "2nd Band Color:",
                        selected = b2,
                        options = listOf(
                            ResistorColor.BLACK, ResistorColor.BROWN, ResistorColor.RED,
                            ResistorColor.ORANGE, ResistorColor.YELLOW, ResistorColor.GREEN,
                            ResistorColor.BLUE, ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.WHITE
                        ),
                        onSelect = { b2 = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    VisualColorSelector(
                        title = "Multiplier Color:",
                        selected = mult,
                        options = listOf(
                            ResistorColor.BLACK, ResistorColor.BROWN, ResistorColor.RED,
                            ResistorColor.ORANGE, ResistorColor.YELLOW, ResistorColor.GREEN,
                            ResistorColor.BLUE, ResistorColor.VIOLET, ResistorColor.GREY,
                            ResistorColor.WHITE, ResistorColor.GOLD, ResistorColor.SILVER
                        ),
                        onSelect = { mult = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    VisualColorSelector(
                        title = "Tolerance Color:",
                        selected = tol,
                        options = listOf(
                            ResistorColor.BROWN, ResistorColor.RED, ResistorColor.ORANGE,
                            ResistorColor.YELLOW, ResistorColor.GREEN, ResistorColor.BLUE,
                            ResistorColor.VIOLET, ResistorColor.GREY, ResistorColor.GOLD, ResistorColor.SILVER
                        ),
                        onSelect = { tol = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Result Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resistance Output",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium, // Poppins Medium
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${calcResult.formattedResistance} ±${calcResult.tolerancePercent}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Min: ${ResistorColorCode.formatResistance(calcResult.minResistance)}  |  Max: ${ResistorColorCode.formatResistance(calcResult.maxResistance)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorDropdown(
    selected: ResistorColor,
    onSelect: (ResistorColor) -> Unit,
    options: List<ResistorColor>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("${selected.colorName} (${selected.digit.takeIf { it >= 0 } ?: selected.multiplier})")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { colorOption ->
                DropdownMenuItem(
                    text = { Text(colorOption.colorName) },
                    onClick = {
                        onSelect(colorOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}

@Composable
fun VisualColorSelector(
    title: String,
    selected: ResistorColor,
    options: List<ResistorColor>,
    onSelect: (ResistorColor) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB),
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        val chunked = options.chunked(5)
        chunked.forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowColors.forEach { color ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelect(color) }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selected == color,
                            onClick = { onSelect(color) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF2563EB),
                                unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = parseColor(color.hex),
                            border = if (color == ResistorColor.WHITE) BorderStroke(1.dp, Color.LightGray) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = color.colorName.lowercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = getTextColorForBackground(color)
                                )
                            }
                        }
                    }
                }
                
                if (rowColors.size < 5) {
                    repeat(5 - rowColors.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private fun getTextColorForBackground(color: ResistorColor): Color {
    return when (color) {
        ResistorColor.YELLOW, ResistorColor.WHITE, ResistorColor.GOLD, ResistorColor.SILVER, ResistorColor.GREY -> Color.Black
        else -> Color.White
    }
}
