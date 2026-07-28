package com.example.electrokit.ui.screens

import android.content.Context
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.ui.components.PcbBackground
import kotlinx.coroutines.delay
import java.util.Calendar

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

    val primaryAccentColor = MaterialTheme.colorScheme.primary
    val context = androidx.compose.ui.platform.LocalContext.current
    val themePrefs = remember(context) { context.getSharedPreferences("electrokit_theme_prefs", android.content.Context.MODE_PRIVATE) }
    var userName by remember { mutableStateOf(themePrefs.getString("user_name", "Engineer") ?: "Engineer") }

    LaunchedEffect(Unit) {
        val saved = themePrefs.getString("user_name", "Engineer") ?: "Engineer"
        userName = saved.ifBlank { "Engineer" }
    }

    // ── Time-Aware Greeting Generator ──────────────────────────────────────────
    val greetingPrefix = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..22 -> "Good Evening"
            else -> "Hello"
        }
    }
    val prefixText = "$greetingPrefix, "
    val highlightText = userName.ifBlank { "Engineer" }
    val fullGreetingText = "$prefixText$highlightText"

    // ── Typewriter Animation State ──────────────────────────────────────────────
    var typedLength by remember { mutableStateOf(0) }
    LaunchedEffect(fullGreetingText) {
        typedLength = 0
        for (i in 1..fullGreetingText.length) {
            delay(38L)
            typedLength = i
        }
    }
    val isTypingComplete = typedLength >= fullGreetingText.length

    val displayedPrefixText = prefixText.take(typedLength)
    val displayedHighlightText = if (typedLength > prefixText.length) {
        highlightText.take(typedLength - prefixText.length)
    } else ""

    // Blinking Cursor | Animation
    val cursorTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by cursorTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    // ── Accent Color Gradient & Soft Pulse Wave Animation for "Engineer" ─────
    val textGlowTransition = rememberInfiniteTransition(label = "text_glow")
    val textGradientShift by textGlowTransition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "text_shift"
    )

    val engineerPulseScale by textGlowTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "engineer_pulse"
    )

    // Dynamic Brush gradient sweep linked to active Accent Color
    val textAccentBrush = remember(primaryAccentColor, textGradientShift) {
        Brush.linearGradient(
            colors = listOf(
                primaryAccentColor,
                primaryAccentColor.copy(alpha = 0.7f),
                Color(0xFF00E5FF),
                primaryAccentColor
            ),
            start = Offset(textGradientShift, 0f),
            end = Offset(textGradientShift + 300f, 0f)
        )
    }

    // ── Waving Hand 👋 Rotation Animation ─────────────────────────────────────
    val waveTransition = rememberInfiniteTransition(label = "hand_wave")
    val waveAngle by waveTransition.animateFloat(
        initialValue = -14f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_angle"
    )

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

    // Search Bar Glow
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

    val whatsNewPrefs = remember(context) { context.getSharedPreferences("electrokit_whats_new_prefs", Context.MODE_PRIVATE) }
    var showWhatsNewDialog by remember {
        mutableStateOf(whatsNewPrefs.getString("last_seen_version", "") != "4.0.2")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle vector PCB background pattern
        PcbBackground(color = primaryAccentColor)

        if (showWhatsNewDialog) {
            AlertDialog(
                onDismissRequest = {
                    whatsNewPrefs.edit().putString("last_seen_version", "4.0.2").commit()
                    showWhatsNewDialog = false
                },
                title = {
                    Text(
                        text = "What's New in v4.0.2 🚀",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Here is what's new and improved in today's major ElectroKit update:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )

                        WhatsNewFeatureItem(
                            icon = Icons.Default.AutoAwesome,
                            title = "Non-Stop Glowing Card Shimmer",
                            description = "Continuous infinite glassmorphic shimmer sweep across all cards without turning dark or black."
                        )
                        WhatsNewFeatureItem(
                            icon = Icons.Default.Bolt,
                            title = "12-Point Electric Sparks Splash",
                            description = "New logo animation with radiating electric sparks & dynamic accent theme integration."
                        )
                        WhatsNewFeatureItem(
                            icon = Icons.Default.AccountCircle,
                            title = "Custom User Profile Name",
                            description = "Personalize your greeting name in Settings (e.g. Good Morning/Evening, Dilip 👋)."
                        )
                        WhatsNewFeatureItem(
                            icon = Icons.Default.Highlight,
                            title = "Accent Highlight Input Fields",
                            description = "Sleek glassmorphic borders on all input boxes, search bars, and dropdown selectors."
                        )
                        WhatsNewFeatureItem(
                            icon = Icons.Default.Storage,
                            title = "Expanded Components DB",
                            description = "Full datasheet catalog re-seeded with 400+ electronics components & pinouts."
                        )
                        WhatsNewFeatureItem(
                            icon = Icons.Default.Waves,
                            title = "Subtle Ambient PCB Waves",
                            description = "Toned-down gentle circuit background motion for a calm, relaxing tech aesthetic."
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            whatsNewPrefs.edit().putString("last_seen_version", "4.0.2").commit()
                            showWhatsNewDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Awesome, Let's Go! 🚀", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ── Animated Header (Accent Gradient Shimmer + Typewriter + Wave Pulse) ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Prefix text ("Good Evening, ")
                        if (displayedPrefixText.isNotBlank()) {
                            Text(
                                text = displayedPrefixText,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Highlight word ("Engineer") with Accent Gradient Shimmer & Soft Pulse Wave
                        if (displayedHighlightText.isNotBlank()) {
                            Text(
                                text = displayedHighlightText,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(brush = textAccentBrush),
                                modifier = Modifier.graphicsLayer(
                                    scaleX = engineerPulseScale,
                                    scaleY = engineerPulseScale
                                )
                            )
                        }

                        // Typewriter Cursor |
                        if (!isTypingComplete) {
                            Text(
                                text = "|",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryAccentColor.copy(alpha = cursorAlpha)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Animated Waving Hand Emoji 👋
                        Text(
                            text = "👋",
                            fontSize = 22.sp,
                            modifier = Modifier.graphicsLayer(
                                rotationZ = waveAngle,
                                transformOrigin = TransformOrigin(0.7f, 0.7f)
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Welcome to ElectroKit Offline Toolkit",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Pulsing + Glowing Processor Icon
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
                            .border(BorderStroke(1.dp, primaryAccentColor.copy(alpha = 0.2f)), RoundedCornerShape(14.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "ElectroKit Logo",
                                tint = primaryAccentColor,
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
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx(), 28.dp.toPx())
                                )
                            )
                        }

                        val pathMeasure = androidx.compose.ui.graphics.PathMeasure()
                        pathMeasure.setPath(path, false)
                        val totalLength = pathMeasure.length

                        val segmentLength = 70.dp.toPx()
                        val startDistance = searchProgress * totalLength
                        val endDistance = (startDistance + segmentLength) % totalLength

                        val segmentPath = androidx.compose.ui.graphics.Path()
                        if (endDistance > startDistance) {
                            pathMeasure.getSegment(startDistance, endDistance, segmentPath, true)
                        } else {
                            pathMeasure.getSegment(startDistance, totalLength, segmentPath, true)
                            pathMeasure.getSegment(0f, endDistance, segmentPath, true)
                        }

                        drawPath(
                            path = segmentPath,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF).copy(alpha = 0.05f),
                                    Color(0xFF00E5FF).copy(alpha = 0.85f),
                                    primaryAccentColor.copy(alpha = 0.9f),
                                    primaryAccentColor.copy(alpha = 0.05f)
                                )
                            ),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search any tool, calculator or component...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = primaryAccentColor) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        } else {
                            IconButton(onClick = { performSearch() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Perform Search",
                                    tint = primaryAccentColor
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { performSearch() }),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier.fillMaxSize(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                items(categoryChips) { chip ->
                    val isSelected = selectedCategoryChip == chip
                    val bgCol by animateColorAsState(
                        targetValue = if (isSelected) primaryAccentColor else MaterialTheme.colorScheme.surface,
                        label = "chip_bg"
                    )
                    val txtCol by animateColorAsState(
                        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        label = "chip_txt"
                    )

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryChip = chip },
                        label = {
                            Text(
                                text = chip,
                                color = txtCol,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = bgCol,
                            selectedContainerColor = bgCol
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            selectedBorderColor = primaryAccentColor
                        )
                    )
                }
            }

            // Tools Grid: 2-Column responsive grid
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredTools, key = { it.title }) { tool ->
                    ToolGridCard(
                        tool = tool,
                        isScrolling = isScrolling,
                        onClick = { onNavigateTo(tool.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun ToolGridCard(
    tool: ToolItem,
    isScrolling: Boolean,
    onClick: () -> Unit
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    val primaryAccent = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing),
        label = "card_scale"
    )

    // Floating Icon Y translation animation
    val infiniteTransition = rememberInfiniteTransition(label = "float_${tool.title}")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isScrolling) 0f else -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    // Continuous glassmorphic shimmer gradient sweep across the card surface
    val shimmerShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 700f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "card_shimmer"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = primaryAccent.copy(alpha = 0.20f),
                spotColor = primaryAccent.copy(alpha = 0.25f)
            )
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .border(
                BorderStroke(1.dp, primaryAccent.copy(alpha = 0.22f)),
                RoundedCornerShape(22.dp)
            )
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            surfaceColor,
                            primaryAccent.copy(alpha = 0.14f),
                            surfaceColor,
                            primaryAccent.copy(alpha = 0.08f)
                        ),
                        start = Offset(shimmerShift - 350f, shimmerShift - 350f),
                        end = Offset(shimmerShift, shimmerShift)
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Floating Action Icon with glowing background aura
                Box(
                    modifier = Modifier
                        .graphicsLayer(translationY = floatY)
                        .size(46.dp)
                        .background(primaryAccent.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = primaryAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Text Content
                Column {
                    Text(
                        text = tool.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = tool.description,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f),
                        maxLines = 2,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsNewFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}
