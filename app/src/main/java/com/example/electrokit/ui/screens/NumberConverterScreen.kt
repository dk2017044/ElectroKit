package com.example.electrokit.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.converters.ConverterUtils
import com.example.electrokit.domain.converters.NumberSystem
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberConverterScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var fromSystem by remember { mutableStateOf(NumberSystem.DECIMAL) }
    var toSystem by remember { mutableStateOf(NumberSystem.BINARY) }
    var inputStr by remember { mutableStateOf("255") }

    val errorMessage = remember(inputStr, fromSystem) {
        if (inputStr.isBlank()) null else ConverterUtils.validateInput(inputStr, fromSystem)
    }

    val convertedResult = remember(inputStr, fromSystem, toSystem, errorMessage) {
        if (errorMessage == null && inputStr.isNotBlank()) {
            try {
                ConverterUtils.convert(inputStr, fromSystem, toSystem)
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }

    val decVal = remember(inputStr, fromSystem, errorMessage) {
        if (errorMessage == null && inputStr.isNotBlank()) try { ConverterUtils.convert(inputStr, fromSystem, NumberSystem.DECIMAL) } catch (e: Exception) { "" } else ""
    }
    val binVal = remember(inputStr, fromSystem, errorMessage) {
        if (errorMessage == null && inputStr.isNotBlank()) try { ConverterUtils.convert(inputStr, fromSystem, NumberSystem.BINARY) } catch (e: Exception) { "" } else ""
    }
    val octVal = remember(inputStr, fromSystem, errorMessage) {
        if (errorMessage == null && inputStr.isNotBlank()) try { ConverterUtils.convert(inputStr, fromSystem, NumberSystem.OCTAL) } catch (e: Exception) { "" } else ""
    }
    val hexVal = remember(inputStr, fromSystem, errorMessage) {
        if (errorMessage == null && inputStr.isNotBlank()) try { ConverterUtils.convert(inputStr, fromSystem, NumberSystem.HEXADECIMAL) } catch (e: Exception) { "" } else ""
    }

    fun copyToClipboard(text: String, label: String) {
        if (text.isNotBlank()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Number Converter", fontWeight = FontWeight.SemiBold) },
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("From Base", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2563EB))
                        SystemDropdown(selected = fromSystem, onSelect = { fromSystem = it })
                    }

                    IconButton(
                        onClick = {
                            val temp = fromSystem
                            fromSystem = toSystem
                            toSystem = temp
                        },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Swap Bases", tint = Color(0xFF2563EB))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("To Base", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2563EB))
                        SystemDropdown(selected = toSystem, onSelect = { toSystem = it })
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = inputStr,
                    onValueChange = { inputStr = it },
                    label = { Text("Enter ${fromSystem.displayName} Number") },
                    placeholder = {
                        Text(
                            when (fromSystem) {
                                NumberSystem.BINARY -> "e.g. 11111111 (0-1)"
                                NumberSystem.OCTAL -> "e.g. 377 (0-7)"
                                NumberSystem.DECIMAL -> "e.g. 255 (0-9)"
                                NumberSystem.HEXADECIMAL -> "e.g. FF (0-9, A-F)"
                            }
                        )
                    },
                    isError = errorMessage != null,
                    colors = electroKitTextFieldColors(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Result (${toSystem.displayName})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium, // Inter Medium
                                color = Color(0xFF2563EB)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AnimatedContent(
                                targetState = if (convertedResult.isNotBlank()) convertedResult else "---",
                                transitionSpec = {
                                    (fadeIn(animationSpec = tween(600)) + slideInVertically { it / 2 })
                                        .togetherWith(fadeOut(animationSpec = tween(400)) + slideOutVertically { -it / 2 })
                                },
                                label = "digit_fade"
                            ) { targetText ->
                                Text(
                                    text = targetText,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (convertedResult.isNotBlank()) {
                            IconButton(onClick = { copyToClipboard(convertedResult, toSystem.displayName) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Result", tint = Color(0xFF2563EB))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("All Base Conversion Outputs", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))

                BaseOutputRow("Decimal (Base 10)", decVal) { copyToClipboard(decVal, "Decimal") }
                BaseOutputRow("Binary (Base 2)", binVal) { copyToClipboard(binVal, "Binary") }
                BaseOutputRow("Octal (Base 8)", octVal) { copyToClipboard(octVal, "Octal") }
                BaseOutputRow("Hexadecimal (Base 16)", hexVal) { copyToClipboard(hexVal, "Hexadecimal") }
            }
        }
    }
}

@Composable
fun SystemDropdown(
    selected: NumberSystem,
    onSelect: (NumberSystem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = selected.displayName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            NumberSystem.values().forEach { sys ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = sys.displayName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onSelect(sys)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BaseOutputRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, color = Color(0xFF2563EB))
            Text(
                text = value.ifBlank { "---" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium, // Inter Medium style
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (value.isNotBlank()) {
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
