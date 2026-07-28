package com.example.electrokit.ui.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.SeriesParallelCalculator
import com.example.electrokit.ui.components.ElectroKitUnitSelector
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

data class ConnectionModeInfo(
    val title: String,
    val description: String,
    val modeKey: String,
    val icon: ImageVector
)

data class InputRowItem(
    val id: Int,
    var valueStr: String = "",
    var selectedUnit: String = "Ω"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesParallelScreen(onBack: () -> Unit) {
    var selectedConnectionType by remember { mutableStateOf<String?>(null) } // null = Main 3-Card Home-style Grid View
    var componentType by remember { mutableStateOf("Resistors") } // "Resistors", "Capacitors", "Inductors"

    // Default units per component type
    val defaultUnit = when (componentType) {
        "Resistors" -> "Ω"
        "Capacitors" -> "µF"
        "Inductors" -> "mH"
        else -> "Ω"
    }

    val availableUnits = when (componentType) {
        "Resistors" -> listOf("Ω", "kΩ", "MΩ")
        "Capacitors" -> listOf("pF", "nF", "µF", "mF")
        "Inductors" -> listOf("µH", "mH", "H")
        else -> listOf("Ω")
    }

    var nextId by remember { mutableIntStateOf(3) }
    val inputsList = remember {
        mutableStateListOf(
            InputRowItem(1, "10.0", defaultUnit),
            InputRowItem(2, "20.0", defaultUnit)
        )
    }

    // Reset units when switching component type
    LaunchedEffect(componentType) {
        val newDefault = when (componentType) {
            "Resistors" -> "Ω"
            "Capacitors" -> "µF"
            "Inductors" -> "mH"
            else -> "Ω"
        }
        for (i in inputsList.indices) {
            inputsList[i] = inputsList[i].copy(selectedUnit = newDefault)
        }
    }

    // Convert all user inputs into base units (Ohms, Farads, Henries)
    val baseValuesInSI = remember(inputsList.toList(), componentType) {
        inputsList.mapNotNull { item ->
            val num = item.valueStr.replace(',', '.').toDoubleOrNull() ?: return@mapNotNull null
            if (num <= 0) return@mapNotNull null

            val multiplier = getUnitMultiplier(item.selectedUnit, componentType)
            num * multiplier
        }
    }

    val resResult = remember(baseValuesInSI, componentType) {
        if (componentType == "Resistors" || componentType == "Inductors") {
            SeriesParallelCalculator.calculateResistors(baseValuesInSI)
        } else {
            SeriesParallelCalculator.calculateCapacitors(baseValuesInSI)
        }
    }

    // 3 Connection Modes for Landing Page Grid
    val connectionModes = listOf(
        ConnectionModeInfo(
            title = "Series Connection",
            description = "Calculate total series value (R1 + R2 + ...)",
            modeKey = "Series",
            icon = Icons.Default.LinearScale
        ),
        ConnectionModeInfo(
            title = "Parallel Connection",
            description = "Calculate total parallel value (1 / (1/R1 + 1/R2 + ...))",
            modeKey = "Parallel",
            icon = Icons.Default.DensityMedium
        ),
        ConnectionModeInfo(
            title = "Series & Parallel Both",
            description = "Calculate both Series and Parallel totals together side-by-side",
            modeKey = "Both",
            icon = Icons.Default.AccountTree
        )
    )

    BackHandler {
        if (selectedConnectionType != null) {
            selectedConnectionType = null
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedConnectionType) {
                            "Series" -> "Series Calculator"
                            "Parallel" -> "Parallel Calculator"
                            "Both" -> "Series & Parallel Combo"
                            else -> "Series / Parallel Toolkit"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedConnectionType != null) {
                            selectedConnectionType = null
                        } else {
                            onBack()
                        }
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PcbBackground(color = MaterialTheme.colorScheme.primary)

            // ── VIEW MODE 1: Home-Page Style 3-Card Category Grid View ───────────────
            if (selectedConnectionType == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Select Circuit Connection Mode:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(connectionModes, key = { it.modeKey }) { modeInfo ->
                            ConnectionModeCard(
                                modeInfo = modeInfo,
                                onClick = { selectedConnectionType = modeInfo.modeKey }
                            )
                        }
                    }
                }
            }
            // ── VIEW MODE 2: Active Calculator (With Units & Precision Calculations) ──
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Component Type Selector Chips ONLY (Duplicate Mode row removed!)
                    Text(
                        text = "Component Type:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Resistors", "Capacitors", "Inductors").forEach { item ->
                            FilterChip(
                                selected = componentType == item,
                                onClick = { componentType = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Input Rows List with Custom ElectroKit Unit Selectors
                    Text(
                        text = "$componentType Input Values:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    inputsList.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = item.valueStr,
                                onValueChange = { newStr ->
                                    inputsList[index] = item.copy(valueStr = newStr)
                                },
                                label = { Text("${componentType.dropLast(1)} ${index + 1}") },
                                colors = electroKitTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Custom ElectroKit Unit Selector Dropdown
                            ElectroKitUnitSelector(
                                selectedUnit = item.selectedUnit,
                                unitList = availableUnits,
                                onUnitChange = { newUnit ->
                                    inputsList[index] = item.copy(selectedUnit = newUnit)
                                }
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    if (inputsList.size > 1) {
                                        inputsList.removeAt(index)
                                    }
                                },
                                enabled = inputsList.size > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove Component",
                                    tint = if (inputsList.size > 1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }

                    // Add component button row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                inputsList.add(InputRowItem(nextId++, "", defaultUnit))
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Component", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add ${componentType.dropLast(1)}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Calculation Result Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = when (selectedConnectionType) {
                                    "Series" -> "$componentType Series Calculation"
                                    "Parallel" -> "$componentType Parallel Calculation"
                                    else -> "$componentType Series & Parallel Calculation"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            if (resResult != null) {
                                val seriesFormatted = formatEngineeringValue(resResult.seriesTotal, componentType)
                                val parallelFormatted = formatEngineeringValue(resResult.parallelTotal, componentType)

                                when (selectedConnectionType) {
                                    "Series" -> {
                                        ResultValueRow(label = "Total Series Value", value = seriesFormatted)
                                    }
                                    "Parallel" -> {
                                        ResultValueRow(label = "Total Parallel Value", value = parallelFormatted)
                                    }
                                    else -> {
                                        ResultValueRow(label = "Series Total", value = seriesFormatted)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        ResultValueRow(label = "Parallel Total", value = parallelFormatted)
                                    }
                                }
                            } else {
                                Text(
                                    text = "Enter at least one positive component value above to compute total.",
                                    fontSize = 13.sp,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultValueRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ConnectionModeCard(
    modeInfo: ConnectionModeInfo,
    onClick: () -> Unit
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing),
        label = "card_scale"
    )

    com.example.electrokit.ui.components.ElectroKitCard(
        cornerRadius = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = modeInfo.icon,
                            contentDescription = modeInfo.title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = modeInfo.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = modeInfo.description,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun getUnitMultiplier(unit: String, componentType: String): Double {
    return when (componentType) {
        "Resistors" -> when (unit) {
            "kΩ" -> 1_000.0
            "MΩ" -> 1_000_000.0
            else -> 1.0
        }
        "Capacitors" -> when (unit) {
            "pF" -> 1e-12
            "nF" -> 1e-9
            "µF" -> 1e-6
            "mF" -> 1e-3
            else -> 1e-6
        }
        "Inductors" -> when (unit) {
            "µH" -> 1e-6
            "mH" -> 1e-3
            "H" -> 1.0
            else -> 1e-3
        }
        else -> 1.0
    }
}

private fun formatEngineeringValue(valueInBase: Double, componentType: String): String {
    if (valueInBase <= 0.0 || valueInBase.isNaN() || valueInBase.isInfinite()) return "0"
    return when (componentType) {
        "Resistors" -> {
            when {
                valueInBase >= 1_000_000.0 -> String.format("%.3f MΩ", valueInBase / 1_000_000.0)
                valueInBase >= 1_000.0 -> String.format("%.3f kΩ", valueInBase / 1_000.0)
                else -> String.format("%.2f Ω", valueInBase)
            }
        }
        "Capacitors" -> {
            when {
                valueInBase >= 1e-3 -> String.format("%.3f mF", valueInBase / 1e-3)
                valueInBase >= 1e-6 -> String.format("%.3f µF", valueInBase / 1e-6)
                valueInBase >= 1e-9 -> String.format("%.3f nF", valueInBase / 1e-9)
                else -> String.format("%.2f pF", valueInBase / 1e-12)
            }
        }
        "Inductors" -> {
            when {
                valueInBase >= 1.0 -> String.format("%.3f H", valueInBase)
                valueInBase >= 1e-3 -> String.format("%.3f mH", valueInBase / 1e-3)
                else -> String.format("%.2f µH", valueInBase / 1e-6)
            }
        }
        else -> String.format("%.2f", valueInBase)
    }
}
