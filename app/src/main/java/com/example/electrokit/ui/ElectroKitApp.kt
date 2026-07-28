package com.example.electrokit.ui

import android.content.Context
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.data.database.entity.ComponentEntity
import com.example.electrokit.ui.screens.*
import com.example.electrokit.ui.screens.components.ComponentDetailScreen
import com.example.electrokit.ui.screens.components.ComponentListScreen
import com.example.electrokit.ui.theme.ElectroKitTheme
import com.example.electrokit.domain.utils.UpdateManager
import com.example.electrokit.domain.utils.DeviceInfoHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ElectroKitApp() {
    val context = LocalContext.current
    val themePrefs = remember(context) { context.getSharedPreferences("electrokit_theme_prefs", Context.MODE_PRIVATE) }
    val appPrefs = remember(context) { context.getSharedPreferences("electrokit_app_prefs", Context.MODE_PRIVATE) }
    var isDarkTheme by remember { mutableStateOf(themePrefs.getBoolean("is_dark_theme", true)) }
    var selectedAccentColorName by remember { mutableStateOf(themePrefs.getString("accent_color", "Blue") ?: "Blue") }
    val accentColor = remember(selectedAccentColorName) { com.example.electrokit.ui.theme.getAccentColorByName(selectedAccentColorName) }

    var currentScreen by remember { mutableStateOf("splash") }
    var selectedComponent by remember { mutableStateOf<ComponentEntity?>(null) }
    var selectedBottomNavIndex by remember { mutableStateOf(0) }

    // Shared state for component search query, persisting across tab switches
    var sharedSearchQuery by remember { mutableStateOf("") }

    // Quit confirmation double tap timestamp
    var lastBackPressTime by remember { mutableStateOf(0L) }

    // Splash Timer (2 Seconds) + Silent Update check once a week + APK clean up
    LaunchedEffect(Unit) {
        UpdateManager.cleanUpLeftoverApks(context)
        delay(2000)
        currentScreen = "main"

        val lastCheckTime = appPrefs.getLong("last_update_check_time", 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastCheckTime > 604800000L) { // 7 days
            UpdateManager.checkForUpdates(context) { result ->
                result.onSuccess { info ->
                    if (info.isNewer) {
                        UpdateManager.showUpdateNotification(context, info.latestVersion)
                    }
                    appPrefs.edit().putLong("last_update_check_time", currentTime).apply()
                }
            }
        }
    }

    var showWhatsNewDialog by remember {
        val currentVersionCode = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: Exception) {
            0
        }
        val lastVersionCode = appPrefs.getInt("last_version_code", -1)
        val isFirstLaunchAfterUpdate = lastVersionCode != -1 && currentVersionCode > lastVersionCode
        
        // Save current version code so we don't show the dialog again
        appPrefs.edit().putInt("last_version_code", currentVersionCode).apply()
        
        mutableStateOf(isFirstLaunchAfterUpdate)
    }

    ElectroKitTheme(darkTheme = isDarkTheme, accentColor = accentColor) {
        if (showWhatsNewDialog) {
            AlertDialog(
                onDismissRequest = { showWhatsNewDialog = false },
                title = {
                    Text(
                        text = "Welcome to ElectroKit v${DeviceInfoHelper.getAppVersion(context)}! 🎉",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "What's New in this Version:",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        val bulletPoints = listOf(
                            "Theme Persistence: Light and Dark theme selections are now automatically saved and restored on app launch.",
                            "Automated GitHub Updates: Check for updates securely directly from GitHub Releases, with background downloading and automatic install prompting.",
                            "Gradle Archiving Architected: Separated debug and release build tasks to automatically compile and copy versioned release binaries."
                        )
                        
                        bulletPoints.forEach { point ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = point,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showWhatsNewDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Got it!", color = Color.White)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                "splash" -> {
                    com.example.electrokit.ui.screens.splash.SplashScreen()
                }

                "component_detail" -> {
                    val comp = selectedComponent
                    if (comp != null) {
                        ComponentDetailScreen(
                            component = comp,
                            onBack = { currentScreen = "main" }
                        )
                    } else {
                        currentScreen = "main"
                    }
                }

                "ohms_law" -> {
                    OhmsLawScreen(onBack = { currentScreen = "main" })
                }

                "resistor_color" -> {
                    ResistorColorScreen(onBack = { currentScreen = "main" })
                }

                "series_parallel" -> {
                    SeriesParallelScreen(onBack = { currentScreen = "main" })
                }

                "number_converter" -> {
                    NumberConverterScreen(onBack = { currentScreen = "main" })
                }

                "led_resistor" -> {
                    LedResistorScreen(onBack = { currentScreen = "main" })
                }

                "main" -> {
                    // Double press back to exit application handler
                    val currentView = LocalView.current
                    BackHandler {
                        if (selectedBottomNavIndex != 0) {
                            selectedBottomNavIndex = 0
                        } else {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastBackPressTime < 2000) {
                                (context as? android.app.Activity)?.finish()
                            } else {
                                lastBackPressTime = currentTime
                                try {
                                    currentView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                } catch (e: Exception) {}
                                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Scaffold(
                        bottomBar = {
                            // Custom Sliding Bottom Dock Navigation Bar
                            val primaryColor = MaterialTheme.colorScheme.primary
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .padding(horizontal = 24.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(28.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 6.dp,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .border(
                                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                            RoundedCornerShape(28.dp)
                                        )
                                ) {
                                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                        val totalWidth = maxWidth
                                        val itemWidth = totalWidth / 4

                                        // Sliding Active Indicator Pill with Accent Color
                                        val pillOffset by animateDpAsState(
                                            targetValue = itemWidth * selectedBottomNavIndex + (itemWidth - 44.dp) / 2,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            ),
                                            label = "dock_pill_slide"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .offset(x = pillOffset)
                                                .width(44.dp)
                                                .padding(vertical = 10.dp)
                                                .background(
                                                    color = primaryColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(18.dp)
                                                )
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Tab 0: Home
                                            HomeTabIcon(
                                                selected = selectedBottomNavIndex == 0,
                                                onClick = { selectedBottomNavIndex = 0 }
                                            )

                                            // Tab 1: Components Library
                                            ComponentsTabIcon(
                                                selected = selectedBottomNavIndex == 1,
                                                onClick = { selectedBottomNavIndex = 1 }
                                            )

                                            // Tab 2: Favorites
                                            FavoritesTabIcon(
                                                selected = selectedBottomNavIndex == 2,
                                                onClick = { selectedBottomNavIndex = 2 }
                                            )

                                            // Tab 3: Settings & About
                                            SettingsTabIcon(
                                                selected = selectedBottomNavIndex == 3,
                                                onClick = { selectedBottomNavIndex = 3 }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = selectedBottomNavIndex,
                                transitionSpec = {
                                    if (targetState > initialState) {
                                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                                slideOutHorizontally { width -> -width } + fadeOut()
                                    } else {
                                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                                slideOutHorizontally { width -> width } + fadeOut()
                                    }.using(SizeTransform(clip = false))
                                },
                                label = "bottom_nav_transition"
                            ) { index ->
                                when (index) {
                                    0 -> HomeScreen(
                                        onNavigateTo = { destination ->
                                            if (destination.startsWith("components")) {
                                                val query = if (destination.contains("?query=")) {
                                                    destination.substringAfter("?query=")
                                                } else {
                                                    ""
                                                }
                                                sharedSearchQuery = query
                                                selectedBottomNavIndex = 1
                                            } else if (destination == "favorites") {
                                                selectedBottomNavIndex = 2
                                            } else if (destination == "settings" || destination == "support") {
                                                selectedBottomNavIndex = 3
                                            } else {
                                                currentScreen = destination
                                            }
                                        }
                                    )

                                    1 -> ComponentListScreen(
                                        searchQuery = sharedSearchQuery,
                                        onSearchQueryChange = { sharedSearchQuery = it },
                                        onSelectComponent = { comp ->
                                            selectedComponent = comp
                                            currentScreen = "component_detail"
                                        },
                                        onBack = { selectedBottomNavIndex = 0 }
                                    )

                                    2 -> FavoritesScreen(
                                        onNavigateTo = { destination ->
                                            if (destination == "components") {
                                                selectedBottomNavIndex = 1
                                            } else {
                                                currentScreen = destination
                                            }
                                        },
                                        onSelectComponent = { comp ->
                                            selectedComponent = comp
                                            currentScreen = "component_detail"
                                        }
                                    )

                                    3 -> SettingsScreen(
                                        onBack = { selectedBottomNavIndex = 0 },
                                        isDarkTheme = isDarkTheme,
                                        onThemeToggle = { dark ->
                                            isDarkTheme = dark
                                            themePrefs.edit().putBoolean("is_dark_theme", dark).apply()
                                        },
                                        selectedAccentColor = selectedAccentColorName,
                                        onAccentColorChange = { colorName ->
                                            selectedAccentColorName = colorName
                                            themePrefs.edit().putString("accent_color", colorName).apply()
                                        }
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

@Composable
fun HomeTabIcon(selected: Boolean, onClick: () -> Unit) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val rippleProgress = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary

    fun triggerAnimation() {
        scope.launch {
            launch {
                scale.snapTo(1f)
                scale.animateTo(1.2f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                scale.animateTo(1.0f, animationSpec = tween(150, easing = FastOutSlowInEasing))
            }
            launch {
                rippleProgress.snapTo(0f)
                rippleAlpha.snapTo(0.8f)
                rippleProgress.animateTo(1f, animationSpec = tween(350, easing = LinearOutSlowInEasing))
                rippleAlpha.animateTo(0f, animationSpec = tween(150, easing = FastOutSlowInEasing))
            }
        }
    }

    LaunchedEffect(selected) {
        if (selected) {
            triggerAnimation()
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                triggerAnimation()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (rippleAlpha.value > 0f) {
            Canvas(modifier = Modifier.size(54.dp)) {
                drawCircle(
                    color = primaryColor.copy(alpha = rippleAlpha.value),
                    radius = (size.width / 2f) * rippleProgress.value
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = if (selected) primaryColor else Color(0xFF9CA3AF),
            modifier = Modifier
                .scale(scale.value)
                .size(24.dp)
        )
    }
}

@Composable
fun ComponentsTabIcon(selected: Boolean, onClick: () -> Unit) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val assemblyProgress = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary

    fun triggerAnimation() {
        scope.launch {
            assemblyProgress.snapTo(0f)
            assemblyProgress.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(selected) {
        if (selected) {
            triggerAnimation()
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                triggerAnimation()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (assemblyProgress.value > 0f && assemblyProgress.value < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val center = Offset(w / 2, h / 2)
                val progress = assemblyProgress.value

                // Corner nodes converging to center
                val node1 = Offset(center.x - 14.dp.toPx() * (1f - progress), center.y - 14.dp.toPx() * (1f - progress))
                val node2 = Offset(center.x + 14.dp.toPx() * (1f - progress), center.y - 14.dp.toPx() * (1f - progress))
                val node3 = Offset(center.x - 14.dp.toPx() * (1f - progress), center.y + 14.dp.toPx() * (1f - progress))
                val node4 = Offset(center.x + 14.dp.toPx() * (1f - progress), center.y + 14.dp.toPx() * (1f - progress))

                drawCircle(color = Color(0xFF00E5FF), radius = 3.dp.toPx(), center = node1)
                drawCircle(color = Color(0xFF00E5FF), radius = 3.dp.toPx(), center = node2)
                drawCircle(color = Color(0xFF00E5FF), radius = 3.dp.toPx(), center = node3)
                drawCircle(color = Color(0xFF00E5FF), radius = 3.dp.toPx(), center = node4)

                // Dynamic PCB traces forming circuit block
                drawLine(color = primaryColor.copy(alpha = progress), start = node1, end = node2, strokeWidth = 1.2.dp.toPx())
                drawLine(color = primaryColor.copy(alpha = progress), start = node2, end = node4, strokeWidth = 1.2.dp.toPx())
                drawLine(color = primaryColor.copy(alpha = progress), start = node4, end = node3, strokeWidth = 1.2.dp.toPx())
                drawLine(color = primaryColor.copy(alpha = progress), start = node3, end = node1, strokeWidth = 1.2.dp.toPx())
            }
        }
        Icon(
            imageVector = Icons.Default.DeveloperBoard,
            contentDescription = "Components",
            tint = if (selected) primaryColor else Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun FavoritesTabIcon(selected: Boolean, onClick: () -> Unit) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val rippleProgress = remember { Animatable(0f) }
    val rippleAlpha = remember { Animatable(0f) }

    fun triggerAnimation() {
        scope.launch {
            launch {
                scale.snapTo(1f)
                scale.animateTo(1.3f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                scale.animateTo(0.95f, animationSpec = tween(100, easing = FastOutSlowInEasing))
                scale.animateTo(1.1f, animationSpec = tween(80, easing = FastOutSlowInEasing))
                scale.animateTo(1.0f, animationSpec = tween(100, easing = FastOutSlowInEasing))
            }
            launch {
                rippleProgress.snapTo(0f)
                rippleAlpha.snapTo(0.7f)
                rippleProgress.animateTo(1.2f, animationSpec = tween(350, easing = LinearOutSlowInEasing))
                rippleAlpha.animateTo(0f, animationSpec = tween(150, easing = FastOutSlowInEasing))
            }
        }
    }

    LaunchedEffect(selected) {
        if (selected) {
            triggerAnimation()
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                triggerAnimation()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (rippleAlpha.value > 0f) {
            Canvas(modifier = Modifier.size(54.dp)) {
                drawCircle(
                    color = Color(0xFFEF4444).copy(alpha = rippleAlpha.value),
                    radius = (size.width / 2f) * rippleProgress.value
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorites",
            tint = if (selected) Color(0xFFEF4444) else Color(0xFF9CA3AF),
            modifier = Modifier
                .scale(scale.value)
                .size(24.dp)
        )
    }
}

@Composable
fun SettingsTabIcon(selected: Boolean, onClick: () -> Unit) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }

    fun triggerAnimation() {
        scope.launch {
            launch {
                rotation.snapTo(0f)
                rotation.animateTo(180f, animationSpec = tween(400, easing = FastOutSlowInEasing))
            }
            launch {
                glowAlpha.snapTo(0f)
                glowAlpha.animateTo(0.4f, animationSpec = tween(150, easing = FastOutSlowInEasing))
                glowAlpha.animateTo(0f, animationSpec = tween(250, easing = FastOutSlowInEasing))
            }
        }
    }

    LaunchedEffect(selected) {
        if (selected) {
            triggerAnimation()
        }
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                triggerAnimation()
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (glowAlpha.value > 0f) {
            Canvas(modifier = Modifier.size(48.dp)) {
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = glowAlpha.value),
                    radius = size.width / 2f
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = if (selected) MaterialTheme.colorScheme.primary else Color(0xFF9CA3AF),
            modifier = Modifier
                .graphicsLayer(rotationZ = rotation.value)
                .size(24.dp)
        )
    }
}
