package com.example.electrokit.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.ElectroKitApplication
import com.example.electrokit.data.database.FavoritesManager
import com.example.electrokit.data.database.entity.ComponentEntity
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.screens.components.ComponentListItem

data class FavoriteToolItem(
    val title: String,
    val description: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateTo: (String) -> Unit = {},
    onSelectComponent: (ComponentEntity) -> Unit = {}
) {
    val repository = remember { ElectroKitApplication.instance.repository }
    val context = LocalContext.current

    var favoritePartNumbers by remember {
        mutableStateOf(FavoritesManager.getFavorites(context).toList())
    }

    LaunchedEffect(Unit) {
        favoritePartNumbers = FavoritesManager.getFavorites(context).toList()
    }

    val favoriteComponents by repository.getComponentsByPartNumbers(favoritePartNumbers)
        .collectAsState(initial = emptyList())

    val favoriteTools = remember {
        listOf(
            FavoriteToolItem("Ohm's Law", "Voltage, Current & Resistance", "ohms_law", Icons.Default.ElectricBolt),
            FavoriteToolItem("Number System", "Decimal, Binary, Octal & Hex", "number_converter", Icons.Default.SwapHoriz),
            FavoriteToolItem("LED Resistor", "Calculate Current-Limiting Resistor", "led_resistor", Icons.Default.Lightbulb),
            FavoriteToolItem("Component Library", "400 Datasheets & Pinouts", "components", Icons.Default.DeveloperBoard)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Quick Access Tools Section
                item {
                    Text(
                        text = "Quick Access Tools",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ToolGridItem(favoriteTools[0], modifier = Modifier.weight(1f), onClick = { onNavigateTo(favoriteTools[0].route) })
                            ToolGridItem(favoriteTools[1], modifier = Modifier.weight(1f), onClick = { onNavigateTo(favoriteTools[1].route) })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ToolGridItem(favoriteTools[2], modifier = Modifier.weight(1f), onClick = { onNavigateTo(favoriteTools[2].route) })
                            ToolGridItem(favoriteTools[3], modifier = Modifier.weight(1f), onClick = { onNavigateTo(favoriteTools[3].route) })
                        }
                    }
                }

                // 2. Favorite Components Section
                item {
                    Text(
                        text = "Favorite Components",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                if (favoriteComponents.isEmpty()) {
                    item {
                        com.example.electrokit.ui.components.ElectroKitCard(
                            cornerRadius = 20.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Favorite Components Saved",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Go to the Component Library and tap the heart icon on any component to save it here.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(favoriteComponents, key = { it.partNumber }) { comp ->
                        ComponentListItem(
                            component = comp,
                            onClick = { onSelectComponent(comp) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolGridItem(
    item: FavoriteToolItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing),
        label = "grid_item_scale"
    )

    // Gentle floating motion animation for card icons every 4.5 seconds
    val floatTransition = rememberInfiniteTransition(label = "icon_float")
    val floatY by floatTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_y"
    )

    com.example.electrokit.ui.components.ElectroKitCard(
        cornerRadius = 20.dp,
        modifier = modifier
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
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .graphicsLayer(translationY = floatY)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = item.description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
