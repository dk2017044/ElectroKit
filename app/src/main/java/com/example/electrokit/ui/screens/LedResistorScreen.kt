package com.example.electrokit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.LedResistorCalculator
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

data class LedPreset(val colorName: String, val chipColor: Color, val vf: String, val ma: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedResistorScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    var supplyV by remember { mutableStateOf("9.0") }
    var ledVf by remember { mutableStateOf("2.0") }
    var ledCurrentmA by remember { mutableStateOf("20.0") }

    val presets = remember {
        listOf(
            LedPreset("Red", Color(0xFFEF4444), "2.0", "20.0"),
            LedPreset("Green", Color(0xFF10B981), "2.2", "20.0"),
            LedPreset("Yellow", Color(0xFFF59E0B), "2.1", "20.0"),
            LedPreset("Blue", Color(0xFF3B82F6), "3.2", "20.0"),
            LedPreset("White", Color(0xFFE5E7EB), "3.2", "20.0")
        )
    }
    var selectedPresetName by remember { mutableStateOf<String?>(null) }

    val vs = supplyV.replace(',', '.').toDoubleOrNull() ?: 0.0
    val vf = ledVf.replace(',', '.').toDoubleOrNull() ?: 0.0
    val ma = ledCurrentmA.replace(',', '.').toDoubleOrNull() ?: 0.0

    val calcResult = remember(vs, vf, ma) {
        LedResistorCalculator.calculate(vs, vf, ma)
    }

    var isLedGlowing by remember { mutableStateOf(false) }
    val glowAlpha by animateFloatAsState(
        targetValue = if (isLedGlowing) 0.6f else 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "led_glow_breath"
    )

    LaunchedEffect(calcResult) {
        isLedGlowing = calcResult != null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LED Resistor Calculator", fontWeight = FontWeight.SemiBold) },
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
            ) {
                // Preset LED selector
                Text(
                    text = "Preset LED Color",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        FilterChip(
                            selected = selectedPresetName == preset.colorName,
                            onClick = {
                                selectedPresetName = preset.colorName
                                ledVf = preset.vf
                                ledCurrentmA = preset.ma
                            },
                            label = { Text(preset.colorName, fontSize = 12.sp) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(preset.chipColor, CircleShape)
                                )
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = supplyV,
                    onValueChange = { supplyV = it },
                    label = { Text("Supply Voltage (Vs in Volts)") },
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ledVf,
                    onValueChange = {
                        ledVf = it
                        selectedPresetName = null
                    },
                    label = { Text("LED Forward Voltage (Vf in Volts)") },
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = ledCurrentmA,
                    onValueChange = {
                        ledCurrentmA = it
                        selectedPresetName = null
                    },
                    label = { Text("LED Forward Current (If in mA)") },
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Clean LED status indicator — circle background + bulb icon, no floating animation
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = if (isLedGlowing) Color(0xFF00BCD4).copy(alpha = 0.85f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "LED Light Bulb",
                            tint = if (isLedGlowing) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isLedGlowing) "LED Status: Glowing" else "LED Status: Off",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isLedGlowing) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Calculation Result",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium, // Poppins Medium
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (calcResult != null) {
                            Text(
                                text = "Exact Resistor: ${calcResult.calculatedResistance} Ω\n" +
                                       "Nearest E24 Standard: ${calcResult.standardResistance} Ω\n" +
                                       "Recommended Power Rating: ${calcResult.minimumPowerRating} W\n" +
                                       "Actual Power Dissipation: ${calcResult.actualPowerDissipation} W",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                        } else {
                            Text(
                                text = "Please enter valid supply voltage exceeding LED forward voltage.",
                                fontSize = 14.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }
    }
}
