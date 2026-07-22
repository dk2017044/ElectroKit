package com.example.electrokit.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.calculations.SmdResistorCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmdCodeScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    var smdCodeInput by remember { mutableStateOf("472") }

    val decodedResult = remember(smdCodeInput) {
        SmdResistorCode.decode(smdCodeInput)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SMD Resistor Code Finder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = smdCodeInput,
                onValueChange = { smdCodeInput = it },
                label = { Text("Enter SMD Code (e.g. 472, 1002, 4R7, 01A)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Decoded Resistance",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (decodedResult != null) {
                        Text(
                            text = "Resistance: ${decodedResult.formattedResistance}\n" +
                                   "Tolerance: ${decodedResult.tolerance}\n" +
                                   "Exact Value: ${decodedResult.resistanceOhms} Ω",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            lineHeight = 24.sp
                        )
                    } else {
                        Text(
                            text = "Enter a valid 3-digit, 4-digit, R-notation, or EIA-96 SMD resistor code.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
