package lk.voltgo.voltgo.ui.screens.main

/**
 * ------------------------------------------------------------
 * File: ReservationDetailsScreen.kt
 * Author: Panchali Samarasinghe
 * Created: October 21, 2025
 * Version: 1.0
 * ------------------------------------------------------------
 */
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.components.VoltGoGradientButton
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.ReservationDetailsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: ReservationDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var qrToShow by remember { mutableStateOf<String?>(null) }

    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Reservation Details", style = MaterialTheme.typography.titleLarge, color = AppColors.DeepNavy, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.DeepNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.load() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = AppColors.DeepNavy)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Couldn’t load reservation", color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text(state.error!!, color = AppColors.TagCancelledText)
                Spacer(Modifier.height(16.dp))
                VoltGoGradientButton(text = "Retry", onClick = { viewModel.load() })
            }

            else -> {
                val data = state.data!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(AppColors.BrandWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, gradientBorder, cardShape),
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(data.stationName, style = MaterialTheme.typography.titleMedium, color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                StatusChip(status = data.status)
                            }
                            Text("Reservation ID: ${data.id}", color = AppColors.ElectricBlue)
                            Text("Date & Time: ${data.date} • ${data.timeRange}", color = AppColors.DeepNavy)
                            if (!data.notes.isNullOrBlank()) Text("Notes: ${data.notes}", color = AppColors.DeepNavy)
                        }
                    }

                    // Actions (NO cancel for EV Owner)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        val canShowQr = (data.status == ReservationStatus.Confirmed || data.status == ReservationStatus.Pending) && !data.qrCode.isNullOrBlank()
                        if (canShowQr) {
                            VoltGoGradientButton(
                                text = "Show QR",
                                leadingIcon = Icons.Outlined.QrCodeScanner,
                                onClick = { qrToShow = data.qrCode },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) { Text("Back") }
                    }

                    // Metadata
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, gradientBorder, cardShape),
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Details", style = MaterialTheme.typography.titleSmall, color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)
                            Text("Station ID: ${data.stationId}", color = AppColors.DeepNavy)
                            Text("Slot ID: ${data.slotId}", color = AppColors.DeepNavy)
                            if (!data.createdAt.isNullOrBlank()) Text("Created: ${data.createdAt}", color = AppColors.DeepNavy)
                            if (!data.confirmedAt.isNullOrBlank()) Text("Confirmed: ${data.confirmedAt}", color = AppColors.DeepNavy)
                            if (!data.completedAt.isNullOrBlank()) Text("Completed: ${data.completedAt}", color = AppColors.DeepNavy)
                            if (!data.cancelledAt.isNullOrBlank()) Text("Cancelled: ${data.cancelledAt}", color = AppColors.DeepNavy)
                        }
                    }
                }
            }
        }

        if (qrToShow != null) {
            QrCodeDialog(qrText = qrToShow!!, onDismiss = { qrToShow = null })
        }
    }
}