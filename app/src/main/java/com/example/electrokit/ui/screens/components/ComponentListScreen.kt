package com.example.electrokit.ui.screens.components

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.electrokit.ElectroKitApplication
import com.example.electrokit.data.database.entity.ComponentEntity
import com.example.electrokit.ui.components.PcbBackground
import com.example.electrokit.ui.components.electroKitTextFieldColors

data class CategoryItemInfo(
    val categoryName: String,
    val count: Int,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentListScreen(
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSelectComponent: (ComponentEntity) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val repository = remember { ElectroKitApplication.instance.repository }
    var selectedComponentState by remember { mutableStateOf<ComponentEntity?>(null) }

    // Grid View is enabled BY DEFAULT
    var isCategoryGridView by remember { mutableStateOf(searchQuery.isBlank()) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val allComponents by repository.getAllComponents().collectAsState(initial = emptyList())

    // Category breakdown list with "All Components" card at position 0
    val gridCategoryItems by remember(allComponents) {
        derivedStateOf {
            val allCard = CategoryItemInfo(
                categoryName = "All Components",
                count = allComponents.size,
                icon = Icons.Default.Apps
            )

            val parsedList = allComponents.groupBy { it.category }
                .filterKeys { it.isNotBlank() }
                .map { (cat, list) ->
                    CategoryItemInfo(
                        categoryName = cat,
                        count = list.size,
                        icon = getCategoryIcon(cat)
                    )
                }
                .sortedBy { it.categoryName }

            listOf(allCard) + parsedList
        }
    }

    // Filtered components: normally shows all components, but filters by selected category if set
    val filteredComponents by remember(searchQuery, selectedCategoryFilter, allComponents) {
        derivedStateOf {
            allComponents.filter { comp ->
                val matchesCategory = selectedCategoryFilter == null || comp.category.equals(selectedCategoryFilter, ignoreCase = true)
                val matchesSearch = searchQuery.isBlank() ||
                        comp.partNumber.contains(searchQuery, ignoreCase = true) ||
                        comp.componentName.contains(searchQuery, ignoreCase = true) ||
                        comp.category.contains(searchQuery, ignoreCase = true) ||
                        comp.description.contains(searchQuery, ignoreCase = true) ||
                        comp.keywordsRaw.contains(searchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
        }
    }

    var visibleCount by remember(searchQuery, selectedCategoryFilter, filteredComponents) { mutableStateOf(20) }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState, filteredComponents, visibleCount) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty()) {
                    val lastVisibleIndex = visibleItems.last().index
                    if (lastVisibleIndex >= visibleCount - 3 && visibleCount < filteredComponents.size) {
                        visibleCount = (visibleCount + 20).coerceAtMost(filteredComponents.size)
                    }
                }
            }
    }

    // Handle Back Press hierarchy
    BackHandler(enabled = true) {
        if (selectedComponentState != null) {
            selectedComponentState = null
        } else if (!isCategoryGridView && searchQuery.isBlank()) {
            isCategoryGridView = true
            selectedCategoryFilter = null
        } else if (selectedCategoryFilter != null) {
            selectedCategoryFilter = null
            isCategoryGridView = true
        } else if (searchQuery.isNotBlank()) {
            onSearchQueryChange("")
        } else {
            onBack()
        }
    }

    if (selectedComponentState != null) {
        ComponentDetailScreen(
            component = selectedComponentState!!,
            onBack = { selectedComponentState = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when {
                                isCategoryGridView -> "Components DB"
                                selectedCategoryFilter != null -> selectedCategoryFilter!!
                                searchQuery.isNotBlank() -> "Search Components DB"
                                else -> "All Components DB"
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (!isCategoryGridView && searchQuery.isBlank()) {
                                isCategoryGridView = true
                                selectedCategoryFilter = null
                            } else if (selectedCategoryFilter != null) {
                                selectedCategoryFilter = null
                                isCategoryGridView = true
                            } else if (searchQuery.isNotBlank()) {
                                onSearchQueryChange("")
                            } else {
                                onBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        // Quick toggle button between Grid View and List View
                        IconButton(onClick = {
                            isCategoryGridView = !isCategoryGridView
                            if (isCategoryGridView) selectedCategoryFilter = null
                        }) {
                            Icon(
                                imageVector = if (isCategoryGridView) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Toggle View Mode",
                                tint = MaterialTheme.colorScheme.primary
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
                PcbBackground(color = MaterialTheme.colorScheme.primary)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    // Search Bar (Always active and searches across all components normally)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            onSearchQueryChange(query)
                            if (query.isNotBlank() && isCategoryGridView) {
                                isCategoryGridView = false
                            }
                        },
                        placeholder = {
                            Text(
                                text = if (selectedCategoryFilter != null) "Search in ${selectedCategoryFilter}..." else "Search Part #, Name, Category...",
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            } else {
                                IconButton(onClick = { keyboardController?.hide() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                        shape = RoundedCornerShape(28.dp),
                        colors = electroKitTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        singleLine = true
                    )

                    // ── VIEW MODE 1: Default Categories Grid View (Includes "All Components" option) ──
                    if (isCategoryGridView && searchQuery.isBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Browse Categories & Parts:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = {
                                selectedCategoryFilter = null
                                isCategoryGridView = false
                            }) {
                                Text("View All (${allComponents.size})", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(gridCategoryItems, key = { it.categoryName }) { catInfo ->
                                CategoryCard(
                                    info = catInfo,
                                    onClick = {
                                        if (catInfo.categoryName == "All Components") {
                                            selectedCategoryFilter = null
                                        } else {
                                            selectedCategoryFilter = catInfo.categoryName
                                        }
                                        isCategoryGridView = false
                                    }
                                )
                            }
                        }
                    }
                    // ── VIEW MODE 2: Component List View (Category Filtered or Search Results) ──
                    else {
                        // Category filter row chips
                        if (searchQuery.isBlank()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // "Grid View" chip
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = {
                                            isCategoryGridView = true
                                            selectedCategoryFilter = null
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.GridView, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        },
                                        label = { Text("Grid View", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary) }
                                    )
                                }

                                // "All" chip
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == null,
                                        onClick = { selectedCategoryFilter = null },
                                        label = { Text("All (${allComponents.size})", fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }

                                // Category chips
                                items(gridCategoryItems.filter { it.categoryName != "All Components" }, key = { "chip_${it.categoryName}" }) { catInfo ->
                                    val isSelected = selectedCategoryFilter.equals(catInfo.categoryName, ignoreCase = true)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCategoryFilter = if (isSelected) null else catInfo.categoryName
                                        },
                                        label = { Text("${catInfo.categoryName} (${catInfo.count})", fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        if (filteredComponents.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No components found matching '$searchQuery'" else "No components found in this category",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        } else {
                            val itemsToShow = filteredComponents.take(visibleCount)
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(itemsToShow, key = { it.partNumber }) { component ->
                                    ComponentListItem(
                                        component = component,
                                        onClick = {
                                            selectedComponentState = component
                                            onSelectComponent(component)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    info: CategoryItemInfo,
    onClick: () -> Unit
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing),
        label = "cat_scale"
    )

    com.example.electrokit.ui.components.ElectroKitCard(
        cornerRadius = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
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
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = info.icon,
                                contentDescription = info.categoryName,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = info.categoryName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = "${info.count} Parts",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ComponentListItem(
    component: ComponentEntity,
    onClick: () -> Unit
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 80, easing = LinearOutSlowInEasing),
        label = "item_scale"
    )

    com.example.electrokit.ui.components.ElectroKitCard(
        cornerRadius = 20.dp,
        modifier = Modifier
            .fillMaxWidth()
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = component.partNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (component.category.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = component.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            if (component.componentName.isNotBlank() && component.componentName != component.partNumber) {
                Text(
                    text = component.componentName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (component.description.isNotBlank()) {
                Text(
                    text = component.description,
                    fontSize = 12.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    val cat = category.lowercase()
    return when {
        cat.contains("voltage") || cat.contains("regulator") -> Icons.Default.ElectricBolt
        cat.contains("sensor") || cat.contains("hall") || cat.contains("ldr") || cat.contains("thermistor") -> Icons.Default.Sensors
        cat.contains("logic") || cat.contains("flip") || cat.contains("shift") || cat.contains("counter") -> Icons.Default.Memory
        cat.contains("rf") || cat.contains("bluetooth") || cat.contains("wifi") || cat.contains("gps") -> Icons.Default.Wifi
        cat.contains("demultiplexer") || cat.contains("multiplexer") -> Icons.Default.ForkRight
        cat.contains("microcontroller") || cat.contains("eeprom") -> Icons.Default.DeveloperBoard
        cat.contains("resistor") || cat.contains("potentiometer") -> Icons.Default.Palette
        cat.contains("capacitor") -> Icons.Default.Layers
        cat.contains("transistor") || cat.contains("mosfet") || cat.contains("bjt") -> Icons.Default.DeviceHub
        cat.contains("diode") || cat.contains("zener") || cat.contains("schottky") -> Icons.Default.FlashOn
        cat.contains("op") || cat.contains("amp") || cat.contains("comparator") -> Icons.Default.GraphicEq
        cat.contains("display") || cat.contains("led") -> Icons.Default.Lightbulb
        cat.contains("motor") || cat.contains("servo") || cat.contains("stepper") -> Icons.Default.SettingsInputComponent
        cat.contains("battery") || cat.contains("charger") -> Icons.Default.BatteryChargingFull
        cat.contains("connector") || cat.contains("switch") || cat.contains("fuse") || cat.contains("relay") -> Icons.Default.Power
        else -> Icons.Default.Category
    }
}
