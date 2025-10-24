package lk.voltgo.voltgo.ui.screens.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.SlotDetailViewModel

// File: SlotDetailScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotDetailScreen(
    viewModel: SlotDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onReserve:(String) ->Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Station Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                    }
                }
                uiState.station != null -> {
                    val st = uiState.station!!
                    val slots = uiState.physicalSlots
                    val totalSlots = uiState.totalSlots

                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header + status tags
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                st.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = AppColors.DeepNavy,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Active/Inactive tag
                                StatusTag(
                                    label = if (st.isActive) "Active" else "Inactive",
                                    bg = if (st.isActive) AppColors.TagCompletedBg else AppColors.TagPendingBg,
                                    fg = if (st.isActive) AppColors.TagCompletedText else AppColors.TagPendingText,
                                    border = if (st.isActive) AppColors.TagCompletedBorder else AppColors.TagPendingBorder
                                )
                                // Type tag
                                StatusTag(
                                    label = st.type?.ifBlank { "General" } ?: "General",
                                    bg = AppColors.TagConfirmedBg,
                                    fg = AppColors.TagConfirmedText,
                                    border = AppColors.TagConfirmedBorder
                                )
                                // Slots tag — uses uiState.totalSlots
                                StatusTag(
                                    label = "Slots: $totalSlots",
                                    bg = AppColors.TagPendingBg,
                                    fg = AppColors.TagPendingText,
                                    border = AppColors.TagPendingBorder
                                )
                            }
                        }

                        // Gradient spec card
                        GradientCard {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    "Station info",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.DeepNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                                InfoRow(title = "Type", value = st.type ?: "-")
                                InfoRow(title = "Location", value = st.location ?: "-")
                                InfoRow(title = "Available slots", value = "${st.availableSlots ?: totalSlots}")
                                InfoRow(title = "Status", value = if (st.isActive) "Active" else "Inactive")
                            }
                        }

                        // --- Connectors section (NEW) ---
                        if (slots.isNotEmpty()) {
                            GradientCard {
                                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Connectors",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = AppColors.DeepNavy,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        slots.forEach { s ->
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        s.label ?: "Slot ${s.number}",
                                                        color = AppColors.DeepNavy,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Text(
                                                        buildString {
                                                            append(s.connectorType ?: "N/A")
                                                            s.maxKw?.let { append(" • ${it}kW") }
                                                        },
                                                        color = AppColors.DeepNavy.copy(alpha = 0.8f),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                StatusTag(
                                                    label = if (s.isActive) "Available" else "Offline",
                                                    bg = if (s.isActive) AppColors.TagCompletedBg else AppColors.TagPendingBg,
                                                    fg = if (s.isActive) AppColors.TagCompletedText else AppColors.TagPendingText,
                                                    border = if (s.isActive) AppColors.TagCompletedBorder else AppColors.TagPendingBorder
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Map (unchanged)
                        val lat = st.latitude ?: 0.0
                        val lng = st.longitude ?: 0.0
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 14f)
                        }
                        GradientCard {
                            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    "Map",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AppColors.DeepNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Box(Modifier.fillMaxWidth().height(220.dp)) {
                                    GoogleMap(
                                        cameraPositionState = cameraPositionState,
                                        uiSettings = MapUiSettings(zoomControlsEnabled = true),
                                        properties = MapProperties(isMyLocationEnabled = false)
                                    ) {
                                        Marker(
                                            state = MarkerState(LatLng(lat, lng)),
                                            title = st.name,
                                            snippet = st.location
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        // Actions
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val anyActiveSlot = st.isActive && (slots.any { it.isActive } || (st.availableSlots ?: 0) > 0)

                            // Navigate
                            Button(
                                onClick = {
                                    if (st.isActive) {
                                        val label = Uri.encode(st.name)
                                        val uri = Uri.parse("google.navigation:q=$lat,$lng($label)&mode=d")
                                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                            setPackage("com.google.android.apps.maps")
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                enabled = st.isActive,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (st.isActive) AppColors.DeepNavy else Color(0xFFD3D3D3),
                                    contentColor = if (st.isActive) AppColors.BrandWhite else Color.Black,
                                    disabledContainerColor = Color(0xFFD3D3D3),
                                    disabledContentColor = Color.Black
                                )
                            ) { Text("Navigate", fontWeight = FontWeight.SemiBold) }

                            // Reserve Slot
                            Button(
                                onClick = { onReserve(st.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                enabled = anyActiveSlot,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (anyActiveSlot) AppColors.ElectricBlue else Color(0xFFD3D3D3),
                                    contentColor = if (anyActiveSlot) AppColors.BrandWhite else Color.Black,
                                    disabledContainerColor = Color(0xFFD3D3D3),
                                    disabledContentColor = Color.Black
                                )
                            ) { Text("Reserve slot", fontWeight = FontWeight.SemiBold) }
                        }
                    }
                }
            }
        }
    }
}

/* -------------------------- Reusables -------------------------- */

@Composable
private fun StatusTag(
    label: String,
    bg: androidx.compose.ui.graphics.Color,
    fg: androidx.compose.ui.graphics.Color,
    border: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(fg)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = fg, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoRow(title: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = AppColors.DeepNavy.copy(alpha = 0.7f))
        Text(value, color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun GradientCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.BrandWhite)
            .border(
                width = 1.25.dp,
                brush = Brush.linearGradient(AppColors.logoBorderGradient),
                shape = shape
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}