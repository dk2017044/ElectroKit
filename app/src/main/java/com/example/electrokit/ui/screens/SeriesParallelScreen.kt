package com.example.electrokit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.SeriesParallelCalculator
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesParallelScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val inputs = remember { mutableStateListOf("10.0", "20.0") }
    var mode by remember { mutableStateOf("Resistors") }

    val values = remember(inputs.toList()) {
        inputs.mapNotNull { it.replace(',', '.').toDoubleOrNull() }
    }

    val resResult = remember(values, mode) {
        if (mode == "Resistors" || mode == "Inductors") {
            SeriesParallelCalculator.calculateResistors(values)
        } else {
            SeriesParallelCalculator.calculateCapacitors(values)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Series / Parallel Calculator", fontWeight = FontWeight.SemiBold) },
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Resistors", "Capacitors", "Inductors").forEach { item ->
                        FilterChip(
                            selected = mode == item,
                            onClick = { mode = item },
                            label = { Text(item) }
                        )
                    }
                }

                // Dynamic input fields list
                inputs.forEachIndexed { index, inputValue ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputs[index] = it },
                            label = { Text("Component ${index + 1} Value") },
                            colors = electroKitTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputs.size > 1) {
                                    inputs.removeAt(index)
                                }
                            },
                            enabled = inputs.size > 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove Component",
                                tint = if (inputs.size > 1) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
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
                        onClick = { inputs.add("") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Component", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Component", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                            text = "$mode Total Calculation",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium, // Poppins Medium
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (resResult != null) {
                            val unit = when (mode) {
                                "Resistors" -> "Ω"
                                "Capacitors" -> "µF"
                                "Inductors" -> "mH"
                                else -> ""
                            }
                            Text(
                                text = "Series Total: ${resResult.seriesTotal} $unit\n" +
                                       "Parallel Total: ${resResult.parallelTotal} $unit",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 26.sp
                            )
                        } else {
                            Text(
                                text = "Enter at least one positive component value above.",
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
