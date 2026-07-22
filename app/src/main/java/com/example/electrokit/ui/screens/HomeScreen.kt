package com.example.electrokit.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.ui.components.PcbBackground

data class ToolItem(
    val title: String,
    val description: String,
    val category: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryChip by remember { mutableStateOf("All") }

    val allTools = remember {
        listOf(
            ToolItem("Ohm’s Law", "Calculate voltage, current, resistance & power", "Calculators", Icons.Default.ElectricBolt, "ohms_law"),
            ToolItem("LED Resistor", "Find the current-limiting resistor for LEDs", "Calculators", Icons.Default.Lightbulb, "led_resistor"),
            ToolItem("Resistor Color", "Decode 4-band resistor color codes easily", "Components", Icons.Default.Palette, "resistor_color"),
            ToolItem("Series/Parallel", "Compute series/parallel values of parts", "Calculators", Icons.Default.DeviceHub, "series_parallel"),
            ToolItem("Number System", "Convert between Binary, Octal, Dec & Hex", "Converters", Icons.Default.SwapHoriz, "number_converter"),
            ToolItem("Components DB", "Search offline pinouts and specifications", "Components", Icons.Default.DeveloperBoard, "components")
        )
    }

    val categoryChips = listOf("All", "Calculators", "Components", "Converters")

    val filteredTools = remember(searchQuery, selectedCategoryChip) {
        allTools.filter { tool ->
            val matchesCategory = selectedCategoryChip == "All" || tool.category == selectedCategoryChip
            val matchesSearch = searchQuery.isBlank() ||
                    tool.title.contains(searchQuery, ignoreCase = true) ||
                    tool.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    fun performSearch() {
        keyboardController?.hide()
        if (searchQuery.isNotBlank()) {
            onNavigateTo("components?query=$searchQuery")
        }
    }

    // Top Right Processor Icon Pulsing Animation
    val procTransition = rememberInfiniteTransition(label = "processor_glow")
    val scale by procTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "proc_scale"
    )

    val glowAlpha by procTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "proc_glow_alpha"
    )

    // Search Bar Glow: soft cyan/electric blue light traveling slowly around border outline once every 7 seconds
    val searchTransition = rememberInfiniteTransition(label = "search_glow")
    val searchProgress by searchTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "search_progress"
    )

    val gridState = rememberLazyGridState()
    val isScrolling = gridState.isScrollInProgress

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle vector PCB background pattern
        PcbBackground(color = Color(0xFF2563EB))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Greeting Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, Engineer 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold, // Poppins SemiBold
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Welcome to ElectroKit Offline Toolkit",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal, // Inter Regular
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Pulsing + Glowing Processor Icon (always visible, no rotation)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(52.dp)
                ) {
                    if (glowAlpha > 0f) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF00E5FF).copy(alpha = glowAlpha * 0.25f), CircleShape)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .size(42.dp)
                            .border(BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.2f)), RoundedCornerShape(14.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "ElectroKit Logo",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar with glass effect and static outline with moving path segment sweep glow
            val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .drawBehind {
                        // 1. Static border outline
                        drawRoundRect(
                            color = outlineColor,
                            size = size,
                            cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx()),
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // 2. Custom path measure segment sweep glow
                        val path = androidx.compose.ui.graphics.Path().apply {
                            addRoundRect(
                                androidx.compose.ui.geometry.RoundRect(
                                    rect = androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
                                    cornerRadius = CornerRadius(28.dp.toPx(), 28.dp.toPx())
                                )
                            )
                        }
                        val pathMeasure = androidx.compose.ui.graphics.PathMeasure()
                        pathMeasure.setPath(path, false)
                        val totalLen = pathMeasure.length
                        val segmentPath = androidx.compose.ui.graphics.Path()

                        val startDist = searchProgress * totalLen
                        val pulseLen = 120.dp.toPx() // Length of traveling cyan light segment

                        if (startDist + pulseLen <= totalLen) {
                            pathMeasure.getSegment(startDist, startDist + pulseLen, segmentPath, true)
                        } else {
                            pathMeasure.getSegment(startDist, totalLen, segmentPath, true)
                            pathMeasure.getSegment(0f, (startDist + pulseLen) % totalLen, segmentPath, true)
                        }

                        drawPath(
                            path = segmentPath,
                            color = Color(0xFF00E5FF),
                            style = Stroke(width = 2.2.dp.toPx())
                        )
                    }
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(28.dp))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search any tool, calculator or component...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = Color(0xFF2563EB)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        } else {
                            IconButton(onClick = { performSearch() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Perform Search",
                                    tint = Color(0xFF2563EB)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { performSearch() }),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxSize(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Category Filter Chips with smooth selection animations
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                items(categoryChips) { chip ->
                    val isSelected = selectedCategoryChip == chip
                    val bgCol by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF2563EB) else MaterialTheme.colorScheme.surface,
                        label = "chip_bg"
                    )
                    val txtCol by animateColorAsState(
                        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        label = "chip_txt"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        color = bgCol,
                        tonalElevation = if (isSelected) 4.dp else 0.dp,
                        modifier = Modifier
                            .clickable { selectedCategoryChip = chip }
                    ) {
                        Text(
                            text = chip,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium, // Inter Medium
                            color = txtCol,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Grid Layout with redesigned Feature Cards
            if (filteredTools.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tools found matching '$searchQuery'",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = gridState,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredTools) { tool ->
                        ToolCardItem(
                            title = tool.title,
                            description = tool.description,
                            icon = tool.icon,
                            isScrolling = isScrolling,
                            onClick = { onNavigateTo(tool.route) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCardItem(
    title: String,
    description: String,
    icon: ImageVector,
    isScrolling: Boolean,
    onClick: () -> Unit
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing),
        label = "card_scale"
    )

    // Shimmer sweep reflection plays slowly once every 12 seconds
    val shimmerTransition = rememberInfiniteTransition(label = "card_shimmer")
    val shimmerOffset by shimmerTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 12000
                -0.5f at 0
                -0.5f at 8000
                1.5f at 10500
                1.5f at 12000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    // Gentle 1-2px floating motion for icons every 4.5 seconds
    val floatTransition = rememberInfiniteTransition(label = "icon_float")
    val floatY by floatTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_y"
    )

    val activeShimmerOffset = if (isScrolling) -0.5f else shimmerOffset

    val surfaceColor = MaterialTheme.colorScheme.surface
    val cardBrush = remember(activeShimmerOffset, surfaceColor) {
        Brush.linearGradient(
            colors = listOf(
                surfaceColor,
                surfaceColor,
                Color(0xFF00E5FF).copy(alpha = 0.08f),
                surfaceColor,
                surfaceColor
            ),
            start = Offset(activeShimmerOffset * 400f, 0f),
            end = Offset((activeShimmerOffset + 0.4f) * 400f, 400f)
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .background(cardBrush)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(translationY = floatY)
                    .size(46.dp)
                    .background(Color(0xFF2563EB).copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium, // Poppins Medium
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
