package com.example.electrokit.ui.screens.support

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.electrokit.R
import com.example.electrokit.ui.components.PcbBackground
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBack: () -> Unit = {},
    viewModel: SupportViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Support & Developer Info",
                        fontWeight = FontWeight.SemiBold, // Poppins SemiBold
                        fontSize = 20.sp
                    )
                },
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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Developer Info Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.profile),
                                    contentDescription = "Developer Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, Color(0xFF2563EB), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Dilip Kumar",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium, // Poppins Medium
                                        color = Color(0xFF2563EB)
                                    )
                                    Text(
                                        text = "Developer & Creator",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF2563EB).copy(alpha = 0.12f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = "ElectroKit v2.0.2",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF2563EB),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 14.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )

                            InfoDetailRow(
                                icon = Icons.Default.School,
                                label = "Role",
                                value = "Electronics Engineering Student at Gp Patna 7"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            InfoDetailRow(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = "dk2017044@hotmail.com",
                                isClickable = true,
                                onClick = { viewModel.sendFeedback(context) }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            InfoDetailRow(
                                icon = Icons.Default.LocationOn,
                                label = "Location",
                                value = "Patna, Bihar, India"
                            )
                        }
                    }
                }

                // 2. About ElectroKit Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF2563EB).copy(alpha = 0.08f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "About ElectroKit",
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = "About ElectroKit",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium, // Poppins Medium
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "ElectroKit is developed to provide students, hobbyists and electronics enthusiasts with a fast, modern and completely offline electronics toolkit. The goal is to make electronic calculations, component references and engineering resources easily accessible anytime, anywhere.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }
                }

                // 3. Support & Feedback Actions Header
                item {
                    Text(
                        text = "Support & Feedback",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium, // Poppins Medium
                        color = Color(0xFF2563EB),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Options List
                item {
                    SupportOptionCard(
                        icon = Icons.Default.Feedback,
                        title = "Send Feedback (Google Form)",
                        description = "Open Google Form directly in browser to share suggestions.",
                        onClick = { viewModel.openGoogleForm(context) }
                    )
                }

                item {
                    SupportOptionCard(
                        icon = Icons.Default.MailOutline,
                        title = "Direct Email Developer",
                        description = "Send an email directly to dk2017044@hotmail.com",
                        onClick = { viewModel.sendFeedback(context) }
                    )
                }

                item {
                    SupportOptionCard(
                        icon = Icons.Default.BugReport,
                        title = "Report a Bug",
                        description = "Found a problem? Submit a bug report.",
                        onClick = { viewModel.reportBug(context) }
                    )
                }

                item {
                    SupportOptionCard(
                        icon = Icons.Default.Memory,
                        title = "Request New Component",
                        description = "Can't find a component? Request it here.",
                        onClick = { viewModel.requestNewComponent(context) }
                    )
                }

                item {
                    SupportOptionCard(
                        icon = Icons.Default.Lightbulb,
                        title = "Request New Feature",
                        description = "Suggest new calculators, tools or features.",
                        onClick = { viewModel.requestNewFeature(context) }
                    )
                }

                item {
                    SupportOptionCard(
                        icon = Icons.Default.Star,
                        title = "Rate ElectroKit",
                        description = "Support the project.",
                        onClick = { viewModel.rateApp(context) }
                    )
                }

                item {
                    SupportOptionCard(
                        icon = Icons.Default.Share,
                        title = "Share ElectroKit APK",
                        description = "Share current app version (v2.0.2) APK file directly with friends via WhatsApp, Bluetooth, LocalSend.",
                        onClick = { viewModel.shareApp(context) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun InfoDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isClickable) Modifier.clickable { onClick() } else Modifier)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF2563EB).copy(alpha = 0.8f),
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isClickable) Color(0xFF2563EB) else MaterialTheme.colorScheme.onSurface,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
fun SupportOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp), ambientColor = Color.LightGray.copy(alpha = 0.15f), spotColor = Color.LightGray.copy(alpha = 0.15f))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2563EB).copy(alpha = 0.08f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium, // Poppins Medium
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
