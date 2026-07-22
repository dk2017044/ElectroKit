package com.example.electrokit.ui.screens.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.data.database.FavoritesManager
import com.example.electrokit.data.database.entity.ComponentEntity
import com.example.electrokit.ui.components.PcbBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetailScreen(
    component: ComponentEntity,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isFavorite by remember { mutableStateOf(FavoritesManager.isFavorite(context, component.partNumber)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = component.componentName.ifBlank { component.partNumber },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isFavorite = FavoritesManager.toggleFavorite(context, component.partNumber)
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            tint = if (isFavorite) Color.Red else Color(0xFF2563EB),
                            contentDescription = "Toggle Favorite"
                        )
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Component Name
                if (component.componentName.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Component Name") {
                            Text(
                                text = component.componentName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium, // Poppins Medium
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }

                // 2. Part Number
                if (component.partNumber.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Part Number") {
                            Text(
                                text = component.partNumber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // 3. Category
                if (component.category.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Category") {
                            Text(text = component.category, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // 4. Type
                if (component.type.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Type") {
                            Text(text = component.type, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // 5. Description
                if (component.description.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Description") {
                            Text(text = component.description, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                // 6. Package
                if (component.packageType.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Package") {
                            Text(text = component.packageType, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // 7. Pin Count
                if (component.pinCount.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Pin Count") {
                            Text(text = component.pinCount, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // 8. Pin Configuration
                if (component.pinConfiguration.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Pin Configuration") {
                            Text(text = component.pinConfiguration, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // 9. Electrical Specifications
                val hasElectricalSpecs = component.maxVoltage.isNotBlank() ||
                        component.minVoltage.isNotBlank() ||
                        component.maxCurrent.isNotBlank() ||
                        component.powerRating.isNotBlank() ||
                        component.frequency.isNotBlank() ||
                        component.operatingTemperature.isNotBlank() ||
                        component.gain.isNotBlank() ||
                        component.resistance.isNotBlank() ||
                        component.capacitance.isNotBlank() ||
                        component.inductance.isNotBlank() ||
                        component.accuracy.isNotBlank()

                if (hasElectricalSpecs) {
                    item {
                        DetailSectionCard(title = "Electrical Specifications") {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (component.maxVoltage.isNotBlank()) SpecRow("Max Voltage", component.maxVoltage)
                                if (component.minVoltage.isNotBlank()) SpecRow("Min Voltage", component.minVoltage)
                                if (component.maxCurrent.isNotBlank()) SpecRow("Max Current", component.maxCurrent)
                                if (component.powerRating.isNotBlank()) SpecRow("Power Rating", component.powerRating)
                                if (component.frequency.isNotBlank()) SpecRow("Frequency", component.frequency)
                                if (component.operatingTemperature.isNotBlank()) SpecRow("Operating Temperature", component.operatingTemperature)
                                if (component.gain.isNotBlank()) SpecRow("Gain", component.gain)
                                if (component.resistance.isNotBlank()) SpecRow("Resistance", component.resistance)
                                if (component.capacitance.isNotBlank()) SpecRow("Capacitance", component.capacitance)
                                if (component.inductance.isNotBlank()) SpecRow("Inductance", component.inductance)
                                if (component.accuracy.isNotBlank()) SpecRow("Accuracy", component.accuracy)
                            }
                        }
                    }
                }

                // 10. Manufacturer
                if (component.manufacturer.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Manufacturer") {
                            Text(text = component.manufacturer, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                // 11. Applications
                if (component.applicationsRaw.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Applications") {
                            Text(text = component.applicationsRaw, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                // 12. Advantages
                if (component.advantagesRaw.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Advantages") {
                            Text(text = component.advantagesRaw, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                // 13. Limitations
                if (component.limitationsRaw.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Limitations") {
                            Text(text = component.limitationsRaw, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                // 14. Equivalent Components
                if (component.equivalentComponentsRaw.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Equivalent Components") {
                            Text(
                                text = component.equivalentComponentsRaw,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }
                }

                // 15. Keywords
                if (component.keywordsRaw.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Keywords") {
                            Text(text = component.keywordsRaw, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                // 16. Datasheet Summary
                if (component.datasheetSummary.isNotBlank()) {
                    item {
                        DetailSectionCard(title = "Datasheet Summary") {
                            Text(text = component.datasheetSummary, fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }
                }

                // 17. Interactive Specs Card
                item {
                    var isExploring by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isExploring) 1.25f else 1.0f,
                        animationSpec = repeatable(
                            iterations = 2,
                            animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        finishedListener = { isExploring = false },
                        label = "magnifier_zoom"
                    )

                    DetailSectionCard(title = "Specs & Exploration") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Interactive Datasheet Explorer",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2563EB)
                                )
                                Text(
                                    text = "Explore dynamic specifications and component database.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(
                                onClick = { isExploring = true },
                                modifier = Modifier
                                    .graphicsLayer(scaleX = scale, scaleY = scale)
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Explore Zoom",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(28.dp)
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
fun DetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2563EB),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            content()
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "• $label:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
