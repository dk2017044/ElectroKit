package com.example.electrokit.ui.screens

import android.content.Context
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.electrokit.R
import com.example.electrokit.domain.utils.DeviceInfoHelper
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.screens.support.InfoDetailRow
import com.example.electrokit.ui.screens.support.SupportOptionCard
import com.example.electrokit.ui.screens.support.SupportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    isDarkTheme: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    selectedAccentColor: String = "Blue",
    onAccentColorChange: (String) -> Unit = {},
    viewModel: SupportViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }

    val themePrefs = remember(context) { context.getSharedPreferences("electrokit_theme_prefs", Context.MODE_PRIVATE) }
    var selectedThemeOption by remember { mutableStateOf(if (isDarkTheme) "Dark" else "Light") }
    var isDynamicColorEnabled by remember { mutableStateOf(themePrefs.getBoolean("is_dynamic_color", true)) }
    var isAnimationsEnabled by remember { mutableStateOf(themePrefs.getBoolean("is_animations_enabled", true)) }
    var isHapticFeedbackEnabled by remember { mutableStateOf(themePrefs.getBoolean("is_haptic_enabled", true)) }
    var isDatasheetPreviewEnabled by remember { mutableStateOf(themePrefs.getBoolean("is_datasheet_preview", true)) }
    var userNameInput by remember { mutableStateOf(themePrefs.getString("user_name", "Engineer") ?: "Engineer") }

    // Dialog visibility states for clean, non-cluttered Settings UI
    var showAboutDeveloperDialog by remember { mutableStateOf(false) }
    var showSupportFeedbackDialog by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (isHapticFeedbackEnabled) {
            try {
                view.performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & About", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = {
                        triggerHaptic()
                        onBack()
                    }) {
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PcbBackground(color = MaterialTheme.colorScheme.primary)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 0. Profile & Personalization Section
                item {
                    SettingsCategoryCard(title = "Profile & Personalization", icon = Icons.Default.AccountCircle) {
                        Text(
                            text = "Greeting User Name (Default: Engineer)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = userNameInput,
                            onValueChange = { newValue ->
                                // Filter: Allow max 16 chars, alphanumeric and spaces only
                                val filtered = newValue.filter { it.isLetterOrDigit() || it == ' ' }.take(16)
                                userNameInput = filtered
                                themePrefs.edit().putString("user_name", filtered.ifBlank { "Engineer" }).apply()
                            },
                            placeholder = { Text("e.g. Dilip (Max 16 chars)") },
                            colors = com.example.electrokit.ui.components.electroKitTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userNameInput.length}/16 characters (Alphanumeric only)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                        )
                    }
                }

                // 1. Appearance Section
                item {
                    SettingsCategoryCard(title = "Appearance", icon = Icons.Default.Palette) {
                        Text("Theme Mode", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Light", "Dark").forEach { option ->
                                FilterChip(
                                    selected = selectedThemeOption == option,
                                    onClick = {
                                        triggerHaptic()
                                        selectedThemeOption = option
                                        onThemeToggle(option == "Dark")
                                    },
                                    label = { Text(option, fontSize = 12.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Accent Color (App Highlight Theme)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            mapOf(
                                "Blue" to Color(0xFF2563EB),
                                "Green" to Color(0xFF10B981),
                                "Purple" to Color(0xFF8B5CF6),
                                "Orange" to Color(0xFFF59E0B),
                                "Red" to Color(0xFFEF4444)
                            ).forEach { (colorName, colorValue) ->
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (selectedAccentColor == colorName) 3.dp else 0.dp,
                                            color = if (selectedAccentColor == colorName) colorValue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            triggerHaptic()
                                            onAccentColorChange(colorName)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = colorValue,
                                        modifier = Modifier.size(28.dp)
                                    ) {}
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        SettingsToggleRow(
                            title = "Dynamic Material You Colors",
                            subtitle = "Adapt UI colors to device wallpaper",
                            checked = isDynamicColorEnabled,
                            onCheckedChange = {
                                triggerHaptic()
                                isDynamicColorEnabled = it
                                themePrefs.edit().putBoolean("is_dynamic_color", it).apply()
                            }
                        )
                    }
                }

                // 2. Display & Feedback Section
                item {
                    SettingsCategoryCard(title = "Display & Feedback", icon = Icons.Default.DisplaySettings) {
                        SettingsToggleRow(
                            title = "Smooth UI Animations",
                            subtitle = "Enable glassmorphic micro-animations",
                            checked = isAnimationsEnabled,
                            onCheckedChange = {
                                triggerHaptic()
                                isAnimationsEnabled = it
                                themePrefs.edit().putBoolean("is_animations_enabled", it).apply()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsToggleRow(
                            title = "Haptic Feedback",
                            subtitle = "Vibrate on button taps, switches & calculations",
                            checked = isHapticFeedbackEnabled,
                            onCheckedChange = {
                                isHapticFeedbackEnabled = it
                                themePrefs.edit().putBoolean("is_haptic_enabled", it).apply()
                                triggerHaptic()
                            }
                        )
                    }
                }

                // 3. Component Library Settings
                item {
                    SettingsCategoryCard(title = "Component Library", icon = Icons.Default.DeveloperBoard) {
                        SettingsToggleRow(
                            title = "Datasheet Quick Preview",
                            subtitle = "Show inline pinout summary cards",
                            checked = isDatasheetPreviewEnabled,
                            onCheckedChange = {
                                triggerHaptic()
                                isDatasheetPreviewEnabled = it
                                themePrefs.edit().putBoolean("is_datasheet_preview", it).apply()
                            }
                        )
                    }
                }

                // 4. ── CLEAN ABOUT & SUPPORT NAVIGATION CARD ─────────────────────
                item {
                    SettingsCategoryCard(title = "About & Support", icon = Icons.Default.Info) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Sleek Option Row: About Developer & App
                            SettingsNavigationRow(
                                title = "About Developer & App",
                                subtitle = "Developer profile, mission & version details",
                                icon = Icons.Default.Person,
                                onClick = {
                                    triggerHaptic()
                                    showAboutDeveloperDialog = true
                                }
                            )

                            // Sleek Option Row: Support & Feedback
                            SettingsNavigationRow(
                                title = "Support & Feedback",
                                subtitle = "Send feedback, report bug & feature requests",
                                icon = Icons.Default.Feedback,
                                onClick = {
                                    triggerHaptic()
                                    showSupportFeedbackDialog = true
                                }
                            )
                        }
                    }
                }

                // 5. Check for Updates Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                            .clickable {
                                triggerHaptic()
                                if (!viewModel.isCheckingForUpdates && !viewModel.isDownloading) viewModel.checkForUpdates(context)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.SystemUpdate,
                                                contentDescription = "Check for Updates",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Check for Updates",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            ) {
                                                Text(
                                                    text = "v${DeviceInfoHelper.getAppVersion(context)}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (viewModel.showUpToDateDialog) "✓ Already installed latest version (v${DeviceInfoHelper.getAppVersion(context)})!" else "Get the latest features and bug fixes directly from GitHub.",
                                            fontSize = 11.sp,
                                            fontWeight = if (viewModel.showUpToDateDialog) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (viewModel.showUpToDateDialog) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                FilledTonalButton(
                                    onClick = {
                                        triggerHaptic()
                                        if (!viewModel.isCheckingForUpdates && !viewModel.isDownloading) viewModel.checkForUpdates(context)
                                    },
                                    enabled = !viewModel.isCheckingForUpdates && !viewModel.isDownloading,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (viewModel.isCheckingForUpdates) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Checking...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Text("Check Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            if (viewModel.isDownloading) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Downloading Update...",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${(viewModel.downloadProgress * 100).toInt()}%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { viewModel.downloadProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // ── MODAL DIALOG 1: Dedicated "About Developer & App" ───────────────────────
    if (showAboutDeveloperDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDeveloperDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "About Developer & App",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showAboutDeveloperDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Developer Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Dilip Kumar",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Instagram
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFE1306C).copy(alpha = 0.12f),
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {
                                                triggerHaptic()
                                                viewModel.openInstagram(context)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Canvas(modifier = Modifier.size(18.dp)) {
                                                val w = size.width
                                                val h = size.height
                                                val strokeW = w * 0.1f
                                                val r = w * 0.28f
                                                drawRoundRect(
                                                    color = Color(0xFFE1306C),
                                                    topLeft = androidx.compose.ui.geometry.Offset(strokeW / 2, strokeW / 2),
                                                    size = androidx.compose.ui.geometry.Size(w - strokeW, h - strokeW),
                                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
                                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
                                                )
                                                drawCircle(
                                                    color = Color(0xFFE1306C),
                                                    radius = w * 0.22f,
                                                    center = androidx.compose.ui.geometry.Offset(w / 2, h / 2),
                                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
                                                )
                                                drawCircle(
                                                    color = Color(0xFFE1306C),
                                                    radius = w * 0.05f,
                                                    center = androidx.compose.ui.geometry.Offset(w * 0.73f, h * 0.27f)
                                                )
                                            }
                                        }
                                    }

                                    // YouTube
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFF0000).copy(alpha = 0.12f),
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {
                                                triggerHaptic()
                                                viewModel.openYouTube(context)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Canvas(modifier = Modifier.size(20.dp)) {
                                                val w = size.width
                                                val h = size.height
                                                drawRoundRect(
                                                    color = Color(0xFFFF0000),
                                                    topLeft = androidx.compose.ui.geometry.Offset(0f, h * 0.15f),
                                                    size = androidx.compose.ui.geometry.Size(w, h * 0.70f),
                                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.25f, h * 0.25f)
                                                )
                                                val path = androidx.compose.ui.graphics.Path().apply {
                                                    moveTo(w * 0.40f, h * 0.35f)
                                                    lineTo(w * 0.62f, h * 0.50f)
                                                    lineTo(w * 0.40f, h * 0.65f)
                                                    close()
                                                }
                                                drawPath(path = path, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "Developer & Creator",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "v${DeviceInfoHelper.getAppVersion(context)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    )

                    InfoDetailRow(
                        icon = Icons.Default.School,
                        label = "Role",
                        value = "Electronics Engineering Student at GP Patna 7"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoDetailRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = "dk2017044@hotmail.com",
                        isClickable = true,
                        onClick = {
                            triggerHaptic()
                            viewModel.sendFeedback(context)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoDetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Location",
                        value = "Patna, Bihar, India"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "About ElectroKit App:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "ElectroKit is developed to provide students, hobbyists and electronics enthusiasts with a fast, modern and completely offline electronics toolkit. The goal is to make electronic calculations, component references and engineering resources easily accessible anytime, anywhere.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDeveloperDialog = false }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // ── MODAL DIALOG 2: Support & Feedback Options ───────────────────────────
    if (showSupportFeedbackDialog) {
        AlertDialog(
            onDismissRequest = { showSupportFeedbackDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Support & Feedback",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showSupportFeedbackDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.Feedback,
                                title = "Send Feedback (Google Form)",
                                description = "Open Google Form directly in browser to share suggestions.",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.openGoogleForm(context)
                                }
                            )
                        }
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.MailOutline,
                                title = "Direct Email Developer",
                                description = "Send an email directly to dk2017044@hotmail.com",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.sendFeedback(context)
                                }
                            )
                        }
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.BugReport,
                                title = "Report a Bug",
                                description = "Found a problem? Submit a bug report.",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.reportBug(context)
                                }
                            )
                        }
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.Memory,
                                title = "Request New Component",
                                description = "Can't find a component? Request it here.",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.requestNewComponent(context)
                                }
                            )
                        }
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.Lightbulb,
                                title = "Request New Feature",
                                description = "Suggest new calculators, tools or features.",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.requestNewFeature(context)
                                }
                            )
                        }
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.Star,
                                title = "Rate ElectroKit",
                                description = "Support the project.",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.rateApp(context)
                                }
                            )
                        }
                        item {
                            SupportOptionCard(
                                icon = Icons.Default.Share,
                                title = "Share ElectroKit",
                                description = "Share the direct one-click download link of the app's latest version with friends.",
                                onClick = {
                                    triggerHaptic()
                                    viewModel.shareApp(context)
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSupportFeedbackDialog = false }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
    if (viewModel.showUpToDateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showUpToDateDialog = false },
            title = {
                Text(
                    text = "You're Up to Date! 🎉",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = "You are already using the latest version of ElectroKit (v${DeviceInfoHelper.getAppVersion(context)}). No update is needed right now!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.showUpToDateDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Got it!", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    val latestUpdate = viewModel.latestUpdateInfo
    if (latestUpdate != null && latestUpdate.isNewer) {
        AlertDialog(
            onDismissRequest = { viewModel.clearUpdateState() },
            title = {
                Text(
                    text = "New Update Available! 🚀",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Version: ${latestUpdate.latestVersion}",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What's New:",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Box(modifier = Modifier.padding(10.dp).verticalScroll(rememberScrollState())) {
                            Text(
                                text = latestUpdate.releaseNotes,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        triggerHaptic()
                        viewModel.startDownload(context, latestUpdate.downloadUrl, latestUpdate.latestVersion, latestUpdate.releaseNotes)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Update Now", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        triggerHaptic()
                        viewModel.clearUpdateState()
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    val allReleases = viewModel.allReleases
    if (allReleases.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.clearAllReleases() },
            title = {
                Text(
                    text = "Version History & Rollback 🔄",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allReleases.size) { index ->
                        val release = allReleases[index]
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "v${release.latestVersion}",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 15.sp
                                    )
                                    Button(
                                        onClick = {
                                            triggerHaptic()
                                            viewModel.startDownload(context, release.downloadUrl, release.latestVersion, release.releaseNotes)
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Download", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                                if (release.releaseNotes.isNotBlank()) {
                                    Text(
                                        text = release.releaseNotes,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp),
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    triggerHaptic()
                    viewModel.clearAllReleases()
                }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun SettingsCategoryCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    com.example.electrokit.ui.components.ElectroKitCard(
        cornerRadius = 20.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(34.dp)
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
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

@Composable
fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                modifier = Modifier.padding(top = 1.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
