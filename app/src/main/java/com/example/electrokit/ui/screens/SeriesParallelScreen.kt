package com.example.electrokit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import com.example.electrokit.domain.calculations.SeriesParallelCalculator
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesParallelScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    var val1Input by remember { mutableStateOf("10.0") }
    var val2Input by remember { mutableStateOf("20.0") }
    var val3Input by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("Resistors") }

    val values = remember(val1Input, val2Input, val3Input) {
        listOfNotNull(
            val1Input.toDoubleOrNull(),
            val2Input.toDoubleOrNull(),
            val3Input.toDoubleOrNull()
        )
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

                OutlinedTextField(
                    value = val1Input,
                    onValueChange = { val1Input = it },
                    label = { Text("Component 1 Value") },
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = val2Input,
                    onValueChange = { val2Input = it },
                    label = { Text("Component 2 Value") },
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = val3Input,
                    onValueChange = { val3Input = it },
                    label = { Text("Component 3 Value (Optional)") },
                    colors = electroKitTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true
                )


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
                            Text(
                                text = "Series Total: ${resResult.seriesTotal}\n" +
                                       "Parallel Total: ${resResult.parallelTotal}",
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
