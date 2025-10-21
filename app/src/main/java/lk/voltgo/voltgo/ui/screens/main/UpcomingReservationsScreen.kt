package lk.voltgo.voltgo.ui.screens.main

/**
 * ------------------------------------------------------------
 * File: UpcomingReservationsScreen.kt
 * Author: Panchali Samarasinghe
 * Created: October 21, 2025
 * Version: 1.0
 *
 * Description:
 *  Screen that shows ONLY upcoming (future) reservations for the EV owner.
 *  Styling matches MyReservationsScreen (cards, gradient borders, status chip).
 *  EV owner CANNOT cancel here; only "Show QR" (if available) and "View".
 * ------------------------------------------------------------
 */

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.components.VoltGoGradientButton
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.UpcomingReservationsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingReservationsScreen(
    onBackClick: () -> Unit,
    onViewDetails: (ReservationUi) -> Unit,
    viewModel: UpcomingReservationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var qrToShow by remember { mutableStateOf<String?>(null) }

    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Upcoming Reservations",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.DeepNavy,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.DeepNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = AppColors.DeepNavy)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BrandWhite)
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    UpcomingErrorState(
                        message = state.error ?: "Something went wrong",
                        onRetry = { viewModel.refresh() }
                    )
                }
                state.items.isEmpty() -> {
                    UpcomingEmptyState()
                }
                else -> {
                    // Optional header card (info)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .border(2.dp, gradientBorder, cardShape),
                        shape = cardShape,
                        colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Next up ⚡",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.DeepNavy,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "These are your upcoming charging sessions. Show the QR at the station.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.ElectricBlue
                            )
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.items, key = { it.id }) { res ->
                            UpcomingReservationCard(
                                res = res,
                                onView = { onViewDetails(res) },
                                onShowQr = {
                                    if (!res.qrCode.isNullOrBlank()) qrToShow = res.qrCode
                                }
                            )
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                }
            }

            if (qrToShow != null) {
                QrCodeDialog(
                    qrText = qrToShow!!,
                    onDismiss = { qrToShow = null }
                )
            }
        }
    }
}

/* ---------- Item card (NO Cancel button) ---------- */
@Composable
private fun UpcomingReservationCard(
    res: ReservationUi,
    onView: () -> Unit,
    onShowQr: () -> Unit
) {
    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, gradientBorder, cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = res.station,
                    style = MaterialTheme.typography.titleSmall,
                    color = AppColors.DeepNavy,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                StatusChip(status = res.status)
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = res.id,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.ElectricBlue,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${res.date}  •  ${res.timeRange}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.DeepNavy
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val canShowQr = (res.status == ReservationStatus.Confirmed || res.status == ReservationStatus.Pending) &&
                        !res.qrCode.isNullOrBlank()

                if (canShowQr) {
                    VoltGoGradientButton(
                        text = "Show QR",
                        leadingIcon = Icons.Outlined.QrCodeScanner,
                        onClick = onShowQr,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Secondary action: View (details/summary screen you already have)
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("View")
                }
            }
        }
    }
}

/* ---------- Error / Empty ---------- */
@Composable
private fun UpcomingErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Couldn’t load upcoming sessions",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.DeepNavy,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TagCancelledText
        )
        Spacer(Modifier.height(16.dp))
        VoltGoGradientButton(text = "Retry", onClick = onRetry)
    }
}

@Composable
private fun UpcomingEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No upcoming sessions",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.DeepNavy,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Booked sessions will appear here when their date is in the future.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.ElectricBlue
        )
    }
}