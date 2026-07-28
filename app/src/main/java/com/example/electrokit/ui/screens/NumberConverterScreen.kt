package com.example.electrokit.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.domain.converters.ConverterUtils
import com.example.electrokit.domain.converters.NumberSystem
import com.example.electrokit.ui.components.ElectroKitUnitSelector
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

data class NumberCategoryCardInfo(
    val title: String,
    val description: String,
    val system: NumberSystem,
    val icon: ImageVector,
    val badge: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberConverterScreen(onBack: () -> Unit) {
    var selectedLandingSystem by remember { mutableStateOf<NumberSystem?>(null) } // null = Main 4-Card Landing View
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var fromSystem by remember { mutableStateOf(NumberSystem.DECIMAL) }
    var toSystem by remember { mutableStateOf(NumberSystem.BINARY) }
    var inputStr by remember { mutableStateOf("255") }

    // When selecting a landing card, set fromSystem automatically
    LaunchedEffect(selectedLandingSystem) {
        if (selectedLandingSystem != null) {
            fromSystem = selectedLandingSystem!!
            toSystem = when (selectedLandingSystem) {
                NumberSystem.DECIMAL -> NumberSystem.BINARY
                NumberSystem.BINARY -> NumberSystem.DECIMAL
                NumberSystem.HEXADECIMAL -> NumberSystem.DECIMAL
                NumberSystem.OCTAL -> NumberSystem.DECIMAL
                else -> NumberSystem.BINARY
            }
        }
    }

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

    val landingCards = listOf(
        NumberCategoryCardInfo(
            title = "Decimal Converter",
            description = "Convert standard Base-10 numbers to Binary, Octal & Hex",
            system = NumberSystem.DECIMAL,
            icon = Icons.Default.Filter1,
            badge = "Base-10"
        ),
        NumberCategoryCardInfo(
            title = "Binary Converter",
            description = "Convert Base-2 binary code (0s and 1s) to Dec, Hex & Oct",
            system = NumberSystem.BINARY,
            icon = Icons.Default.FormatListNumbered,
            badge = "Base-2"
        ),
        NumberCategoryCardInfo(
            title = "Hexadecimal Converter",
            description = "Convert Base-16 hex values (0-9, A-F) to Dec, Bin & Oct",
            system = NumberSystem.HEXADECIMAL,
            icon = Icons.Default.Code,
            badge = "Base-16"
        ),
        NumberCategoryCardInfo(
            title = "Octal Converter",
            description = "Convert Base-8 octal values (0-7) to Dec, Bin & Hex",
            system = NumberSystem.OCTAL,
            icon = Icons.Default.Tag,
            badge = "Base-8"
        )
    )

    BackHandler {
        if (selectedLandingSystem != null) {
            selectedLandingSystem = null
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedLandingSystem != null) {
                            "${fromSystem.displayName} Converter"
                        } else {
                            "Number System Toolkit"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedLandingSystem != null) {
                            selectedLandingSystem = null
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

            // ── VIEW MODE 1: Home-Page Style 4-Card Category Landing Grid ────────────
            if (selectedLandingSystem == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Select Number System Mode:",
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
                        items(landingCards, key = { it.system.name }) { cardInfo ->
                            NumberSystemCategoryCard(
                                cardInfo = cardInfo,
                                onClick = { selectedLandingSystem = cardInfo.system }
                            )
                        }
                    }
                }
            }
            // ── VIEW MODE 2: Active Converter with Live Cards & Custom Dropdown ─────
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // From Base Selector Chips
                    Text(
                        text = "Input Base:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            NumberSystem.DECIMAL,
                            NumberSystem.BINARY,
                            NumberSystem.HEXADECIMAL,
                            NumberSystem.OCTAL
                        ).forEach { sys ->
                            FilterChip(
                                selected = fromSystem == sys,
                                onClick = { fromSystem = sys },
                                label = { Text(sys.displayName, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Input Field & Target Base Dropdown Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputStr,
                            onValueChange = { inputStr = it },
                            label = { Text("Enter ${fromSystem.displayName} Number") },
                            placeholder = {
                                Text(
                                    when (fromSystem) {
                                        NumberSystem.BINARY -> "e.g. 11111111"
                                        NumberSystem.OCTAL -> "e.g. 377"
                                        NumberSystem.DECIMAL -> "e.g. 255"
                                        NumberSystem.HEXADECIMAL -> "e.g. FF"
                                    }
                                )
                            },
                            isError = errorMessage != null,
                            colors = electroKitTextFieldColors(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Custom ElectroKit Unit Selector for Target System
                        ElectroKitUnitSelector(
                            selectedUnit = toSystem.displayName,
                            unitList = listOf("Decimal", "Binary", "Hexadecimal", "Octal"),
                            onUnitChange = { newDisplayName ->
                                toSystem = when (newDisplayName) {
                                    "Decimal" -> NumberSystem.DECIMAL
                                    "Binary" -> NumberSystem.BINARY
                                    "Hexadecimal" -> NumberSystem.HEXADECIMAL
                                    "Octal" -> NumberSystem.OCTAL
                                    else -> NumberSystem.DECIMAL
                                }
                            }
                        )
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                        )
                    }

                    // Selected Target Conversion Output Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target (${toSystem.displayName}) Result:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (convertedResult.isNotBlank()) {
                                    IconButton(onClick = { copyToClipboard(convertedResult, toSystem.displayName) }) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Result",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (convertedResult.isNotBlank()) convertedResult else "Enter a valid number above...",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "All Base Systems Live Equivalent:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    // Live All Base Output Cards
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BaseResultItemCard(title = "Decimal (Base-10)", value = decVal, onCopy = { copyToClipboard(decVal, "Decimal") })
                        BaseResultItemCard(title = "Binary (Base-2)", value = binVal, onCopy = { copyToClipboard(binVal, "Binary") })
                        BaseResultItemCard(title = "Hexadecimal (Base-16)", value = hexVal, onCopy = { copyToClipboard(hexVal, "Hexadecimal") })
                        BaseResultItemCard(title = "Octal (Base-8)", value = octVal, onCopy = { copyToClipboard(octVal, "Octal") })
                    }
                }
            }
        }
    }
}

@Composable
fun BaseResultItemCard(
    title: String,
    value: String,
    onCopy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Text(
                    text = if (value.isNotBlank()) value else "-",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (value.isNotBlank()) {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy $title",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NumberSystemCategoryCard(
    cardInfo: NumberCategoryCardInfo,
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
                            imageVector = cardInfo.icon,
                            contentDescription = cardInfo.title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cardInfo.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = cardInfo.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = cardInfo.description,
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
