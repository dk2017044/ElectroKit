package com.example.electrokit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.ResistorColor
import com.example.electrokit.domain.calculations.ResistorColorCode
import com.example.electrokit.ui.components.PcbBackground

data class ColorCodeRowData(
    val color: ResistorColor,
    val digitsStr: String,
    val multiplierStr: String,
    val toleranceStr: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResistorColorScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    var selectedModeTab by remember { mutableStateOf(0) } // 0: Calculator, 1: Color Chart & Guide

    var b1 by remember { mutableStateOf(ResistorColor.BROWN) }
    var b2 by remember { mutableStateOf(ResistorColor.BLACK) }
    var mult by remember { mutableStateOf(ResistorColor.RED) }
    var tol by remember { mutableStateOf(ResistorColor.GOLD) }

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

    val referenceTableData = remember {
        listOf(
            ColorCodeRowData(ResistorColor.BLACK, "0", "×1", ""),
            ColorCodeRowData(ResistorColor.BROWN, "1", "×10", "±1% (F)"),
            ColorCodeRowData(ResistorColor.RED, "2", "×100", "±2% (G)"),
            ColorCodeRowData(ResistorColor.ORANGE, "3", "×1K", "±0.05% (W)"),
            ColorCodeRowData(ResistorColor.YELLOW, "4", "×10K", "±0.02% (P)"),
            ColorCodeRowData(ResistorColor.GREEN, "5", "×100K", "±0.5% (D)"),
            ColorCodeRowData(ResistorColor.BLUE, "6", "×1M", "±0.25% (C)"),
            ColorCodeRowData(ResistorColor.VIOLET, "7", "×10M", "±0.1% (B)"),
            ColorCodeRowData(ResistorColor.GREY, "8", "×100M", "±0.01% (L)"),
            ColorCodeRowData(ResistorColor.WHITE, "9", "×1G", ""),
            ColorCodeRowData(ResistorColor.GOLD, "", "×0.1", "±5% (J)"),
            ColorCodeRowData(ResistorColor.SILVER, "", "×0.01", "±10% (K)")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resistor Color Code (4-Band)", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Quick Action button to switch to Student Reference Guide
                    IconButton(onClick = { selectedModeTab = if (selectedModeTab == 0) 1 else 0 }) {
                        Icon(
                            imageVector = if (selectedModeTab == 0) Icons.Default.MenuBook else Icons.Default.Calculate,
                            contentDescription = "Toggle Reference Chart",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
            PcbBackground(color = MaterialTheme.colorScheme.primary)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // ── Top Segmented Pill Toggle Switch ─────────────────────────────────────
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calculator Mode Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedModeTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { selectedModeTab = 0 }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = if (selectedModeTab == 0) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Calculator",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedModeTab == 0) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Color Chart & Values Guide Button (Student View)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedModeTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxHeight()
                                .clickable { selectedModeTab = 1 }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = if (selectedModeTab == 1) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Color Chart & Values",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedModeTab == 1) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (selectedModeTab == 0) {
                        // ── MODE 0: Resistor Calculator ──────────────────────────────────
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFD2B48C),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(vertical = 4.dp)
                                .shadow(2.dp, RoundedCornerShape(24.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 40.dp),
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
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${calcResult.formattedResistance} ±${calcResult.tolerancePercent}%",
                                    fontSize = 22.sp,
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
                    } else {
                        // ── MODE 1: Student Color Code Chart & Values Guide ──────────────
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "4-Band Resistor Diagram",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                ResistorGraphicCanvas(
                                    b1 = b1,
                                    b2 = b2,
                                    mult = mult,
                                    tol = tol,
                                    shimmerAlpha = shimmerAlpha
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Reference Table
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Resistor Color Code Table",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Standard 4-Band Values & Tolerances",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Table Header
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Color",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1.3f)
                                        )
                                        Text(
                                            text = "1st, 2nd Band\nSig. Figures",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1.4f)
                                        )
                                        Text(
                                            text = "Multiplier",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        Text(
                                            text = "Tolerance",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1.3f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Table Rows
                                referenceTableData.forEach { rowData ->
                                    val isSelectedBand = rowData.color == b1 || rowData.color == b2 || rowData.color == mult || rowData.color == tol

                                    val bgAnimColor by animateColorAsState(
                                        targetValue = if (isSelectedBand) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else Color.Transparent,
                                        animationSpec = tween(300),
                                        label = "row_bg"
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = bgAnimColor,
                                        border = if (isSelectedBand) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)) else null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Color Chip + Name
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1.3f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clip(CircleShape)
                                                        .background(parseColor(rowData.color.hex))
                                                        .border(
                                                            1.dp,
                                                            if (rowData.color == ResistorColor.WHITE) Color.Gray else Color.Transparent,
                                                            CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = rowData.color.colorName,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelectedBand) FontWeight.Bold else FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            // Digits
                                            Text(
                                                text = rowData.digitsStr,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelectedBand) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1.4f)
                                            )

                                            // Multiplier
                                            Text(
                                                text = rowData.multiplierStr,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelectedBand) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1.2f)
                                            )

                                            // Tolerance
                                            Text(
                                                text = rowData.toleranceStr,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelectedBand) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.End,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (rowData.toleranceStr.isEmpty()) 0.4f else 0.9f),
                                                modifier = Modifier.weight(1.3f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResistorGraphicCanvas(
    b1: ResistorColor,
    b2: ResistorColor,
    mult: ResistorColor,
    tol: ResistorColor,
    shimmerAlpha: Float
) {
    val b1Color = parseColor(b1.hex)
    val b2Color = parseColor(b2.hex)
    val multColor = parseColor(mult.hex)
    val tolColor = parseColor(tol.hex)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Labels & Arrow pointers top row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1st Band\n(${b1.colorName})",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "2nd Band\n(${b2.colorName})",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Multiplier\n(${mult.colorName})",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tolerance\n(${tol.colorName})",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Graphical Resistor Body with Leads & Callout Lines
        val calloutLineColor = MaterialTheme.colorScheme.primary

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
        ) {
            val width = size.width
            val height = size.height

            val bodyLeft = width * 0.16f
            val bodyRight = width * 0.84f
            val bodyTop = height * 0.28f
            val bodyBottom = height * 0.72f
            val centerY = height * 0.5f

            // 1. Draw Lead Wires (Terminal leads on left and right)
            drawLine(
                color = Color.Gray,
                start = Offset(0f, centerY),
                end = Offset(bodyLeft, centerY),
                strokeWidth = 5f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(bodyRight, centerY),
                end = Offset(width, centerY),
                strokeWidth = 5f
            )

            // 2. Draw Resistor Body Shape
            val resistorBodyPath = Path().apply {
                val r = 16f
                moveTo(bodyLeft + r, bodyTop)
                lineTo(bodyRight - r, bodyTop)
                cubicTo(bodyRight, bodyTop, bodyRight, bodyBottom, bodyRight - r, bodyBottom)
                lineTo(bodyLeft + r, bodyBottom)
                cubicTo(bodyLeft, bodyBottom, bodyLeft, bodyTop, bodyLeft + r, bodyTop)
                close()
            }

            drawPath(
                path = resistorBodyPath,
                color = Color(0xFFE8DFCD)
            )
            drawPath(
                path = resistorBodyPath,
                color = Color.DarkGray.copy(alpha = 0.3f),
                style = Stroke(width = 2f)
            )

            // Calculate band positions
            val bandWidth = (bodyRight - bodyLeft) * 0.08f
            val x1 = bodyLeft + (bodyRight - bodyLeft) * 0.18f
            val x2 = bodyLeft + (bodyRight - bodyLeft) * 0.36f
            val xMult = bodyLeft + (bodyRight - bodyLeft) * 0.54f
            val xTol = bodyLeft + (bodyRight - bodyLeft) * 0.78f

            val bHeight = bodyBottom - bodyTop

            // 3. Draw Colored Bands
            fun drawBand(x: Float, color: Color) {
                drawRect(
                    color = color,
                    topLeft = Offset(x - bandWidth / 2, bodyTop),
                    size = Size(bandWidth, bHeight)
                )
                if (shimmerAlpha > 0f) {
                    drawRect(
                        color = Color.White.copy(alpha = shimmerAlpha),
                        topLeft = Offset(x - bandWidth / 2, bodyTop),
                        size = Size(bandWidth, bHeight)
                    )
                }
            }

            drawBand(x1, b1Color)
            drawBand(x2, b2Color)
            drawBand(xMult, multColor)
            drawBand(xTol, tolColor)

            // 4. Draw Callout Arrow Lines pointing down to bands
            val lineYStart = 0f
            val lineYEnd = bodyTop - 2f

            drawLine(calloutLineColor.copy(alpha = 0.5f), Offset(x1, lineYStart), Offset(x1, lineYEnd), strokeWidth = 2f)
            drawLine(calloutLineColor.copy(alpha = 0.5f), Offset(x2, lineYStart), Offset(x2, lineYEnd), strokeWidth = 2f)
            drawLine(calloutLineColor.copy(alpha = 0.5f), Offset(xMult, lineYStart), Offset(xMult, lineYEnd), strokeWidth = 2f)
            drawLine(calloutLineColor.copy(alpha = 0.5f), Offset(xTol, lineYStart), Offset(xTol, lineYEnd), strokeWidth = 2f)

            drawCircle(calloutLineColor, radius = 3f, center = Offset(x1, lineYEnd))
            drawCircle(calloutLineColor, radius = 3f, center = Offset(x2, lineYEnd))
            drawCircle(calloutLineColor, radius = 3f, center = Offset(xMult, lineYEnd))
            drawCircle(calloutLineColor, radius = 3f, center = Offset(xTol, lineYEnd))
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
            color = MaterialTheme.colorScheme.primary,
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
                                selectedColor = MaterialTheme.colorScheme.primary,
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
