package com.example.electrokit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.OhmsLawCalculator
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OhmsLawScreen(onBack: () -> Unit) {
    var targetParameter by remember { mutableStateOf("Resistance (R)") }
    var vInput by remember { mutableStateOf("") }
    var iInput by remember { mutableStateOf("") }
    var rInput by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("Enter input values to compute...") }

    fun recalculate(
        vVal: String = vInput,
        iVal: String = iInput,
        rVal: String = rInput
    ) {
        val v = vVal.replace(',', '.').toDoubleOrNull()
        val i = iVal.replace(',', '.').toDoubleOrNull()
        val r = rVal.replace(',', '.').toDoubleOrNull()

        val calc = when (targetParameter) {
            "Resistance (R)" -> if (v != null && i != null && i != 0.0) OhmsLawCalculator.calculate(v = v, i = i) else null
            "Voltage (V)"    -> if (i != null && r != null) OhmsLawCalculator.calculate(i = i, r = r) else null
            "Current (I)"    -> if (v != null && r != null && r != 0.0) OhmsLawCalculator.calculate(v = v, r = r) else null
            else             -> null
        }

        if (calc != null) {
            resultText = "Voltage: ${calc.voltage} V\n" +
                         "Current: ${calc.current} A\n" +
                         "Resistance: ${calc.resistance} Ω\n" +
                         "Power: ${calc.power} W"
            when (targetParameter) {
                "Resistance (R)" -> rInput = calc.resistance.toString()
                "Voltage (V)"    -> vInput = calc.voltage.toString()
                "Current (I)"    -> iInput = calc.current.toString()
            }
        } else {
            resultText = "Enter input values to compute..."
            when (targetParameter) {
                "Resistance (R)" -> rInput = ""
                "Voltage (V)"    -> vInput = ""
                "Current (I)"    -> iInput = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ohm's Law Calculator", fontWeight = FontWeight.SemiBold) },
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
                Text(
                    text = "Select Parameter to Calculate:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Mode selector chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    listOf("Resistance (R)", "Voltage (V)", "Current (I)").forEach { param ->
                        FilterChip(
                            selected = targetParameter == param,
                            onClick = {
                                targetParameter = param
                                vInput = ""; iInput = ""; rInput = ""
                                resultText = "Enter input values to compute..."
                            },
                            label = { Text(param, fontSize = 12.sp) }
                        )
                    }
                }

                // ── Voltage Field ──────────────────────────────────────────────────
                val isVEnabled = targetParameter != "Voltage (V)"
                OutlinedTextField(
                    value = vInput,
                    onValueChange = { if (isVEnabled) { vInput = it; recalculate(vVal = it) } },
                    label = { Text(if (isVEnabled) "Voltage (V)" else "Voltage (V) — Auto Calculated") },
                    enabled = isVEnabled,
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true
                )

                // ── Current Field ──────────────────────────────────────────────────
                val isIEnabled = targetParameter != "Current (I)"
                OutlinedTextField(
                    value = iInput,
                    onValueChange = { if (isIEnabled) { iInput = it; recalculate(iVal = it) } },
                    label = { Text(if (isIEnabled) "Current (I) in Amperes" else "Current (I) — Auto Calculated") },
                    enabled = isIEnabled,
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    singleLine = true
                )

                // ── Resistance Field ───────────────────────────────────────────────
                val isREnabled = targetParameter != "Resistance (R)"
                OutlinedTextField(
                    value = rInput,
                    onValueChange = { if (isREnabled) { rInput = it; recalculate(rVal = it) } },
                    label = { Text(if (isREnabled) "Resistance (R) in Ohms" else "Resistance (R) — Auto Calculated") },
                    enabled = isREnabled,
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true
                )

                // ── Result Card ────────────────────────────────────────────────────
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            2.dp, RoundedCornerShape(20.dp),
                            ambientColor = Color.LightGray.copy(alpha = 0.15f),
                            spotColor = Color.LightGray.copy(alpha = 0.15f)
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Calculation Result",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resultText,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
