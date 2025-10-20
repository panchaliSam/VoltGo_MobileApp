/**
 * ------------------------------------------------------------
 * File: MyReservationsScreen.kt
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.1
 *
 * Change log:
 *  - Removed dummy data
 *  - Loads from API via ReservationViewModel
 *  - Added category filter tabs (All / Confirmed / Pending / Completed / Cancelled)
 *  - Shows loading & error states
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.ReservationFilter
import lk.voltgo.voltgo.ui.viewmodel.main.ReservationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    onBackClick: () -> Unit,
    onViewDetails: (ReservationUi) -> Unit,
    onCancelReservation: (ReservationUi) -> Unit,
    viewModel: ReservationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var qrToShow by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Reservations",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.DeepNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppColors.DeepNavy)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = AppColors.ElectricBlue)
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
            // Filter row
            FilterTabs(
                active = state.activeFilter,
                onSelect = viewModel::setFilter
            )

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                state.error != null -> {
                    ErrorState(
                        message = state.error ?: "Something went wrong",
                        onRetry = { viewModel.refresh() }
                    )
                }
                state.filtered.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.filtered, key = { it.id }) { res ->
                            ReservationCard(
                                res = res,
                                onClick = { onViewDetails(res) },
                                onCancel = { onCancelReservation(res) },
                                onShowQr = { qrText -> qrToShow = qrText }
                            )
                        }
                    }
                }
            }
            // Show QR dialog if needed
            if (qrToShow != null) {
                QrCodeDialog(
                    qrText = qrToShow!!,
                    onDismiss = { qrToShow = null }
                )
            }
        }
    }
}

/* ---------- Filter Row ---------- */

@Composable
private fun FilterTabs(
    active: ReservationFilter,
    onSelect: (ReservationFilter) -> Unit
) {
    val items = listOf(
        ReservationFilter.All,
        ReservationFilter.Confirmed,
        ReservationFilter.Pending,
        ReservationFilter.Completed,
        ReservationFilter.Cancelled
    )
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    ScrollableTabRow(
        selectedTabIndex = items.indexOf(active),
        edgePadding = 12.dp,
        containerColor = Color.Transparent,
        contentColor = AppColors.DeepNavy,
        indicator = { /* no underline indicator for a cleaner look */ },
        divider = {}
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == items.indexOf(active)
            val shape = RoundedCornerShape(20.dp)
            Tab(
                selected = selected,
                onClick = { onSelect(item) },
                text = {
                    Text(
                        item.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) AppColors.DeepNavy else AppColors.ElectricBlue
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 8.dp)
                    .then(
                        if (selected)
                            Modifier
                                .border(width = 2.dp, brush = gradientBorder, shape = shape)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        else Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
            )
        }
    }
}

/* ---------- Minimal error/empty states ---------- */

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Couldn’t load reservations",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.DeepNavy
        )
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TagCancelledText
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No reservations yet",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.DeepNavy
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "When you make a reservation, it will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TagCancelledText
        )
    }
}

@Composable
private fun ReservationCard(
    res: ReservationUi,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onShowQr: (String) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Surface(
        shape = shape,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, brush = gradientBorder, shape = shape)
            .padding(1.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Station name
            Text(
                text = res.station,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.DeepNavy
            )

            Spacer(Modifier.height(4.dp))

            // Date & time
            Text(
                text = "${res.date} • ${res.timeRange}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.ElectricBlue
            )

            Spacer(Modifier.height(8.dp))

            // Status chip + actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status chip
                val (bg, fg) = when (res.status) {
                    ReservationStatus.Confirmed -> AppColors.TagConfirmedBg to AppColors.TagConfirmedText
                    ReservationStatus.Pending   -> AppColors.TagPendingBg to AppColors.TagPendingText
                    ReservationStatus.Completed -> AppColors.TagCompletedBg to AppColors.TagCompletedText
                    ReservationStatus.Cancelled -> AppColors.TagCancelledBg to AppColors.TagCancelledText
                }
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = res.status.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = fg,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Row {
                    if ((res.status == ReservationStatus.Confirmed || res.status == ReservationStatus.Completed) &&
                        !res.qrCode.isNullOrBlank()
                    ) {
                        TextButton(onClick = { onShowQr(res.qrCode!!) }) {
                            Text("Show QR")
                        }
                    }


                    // Cancel (only for Pending / Confirmed)
                    if (res.status == ReservationStatus.Pending || res.status == ReservationStatus.Confirmed) {
                        TextButton(onClick = onCancel) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QrCodeDialog(
    qrText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = { Text("Reservation QR") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val img = remember(qrText) { generateQrBitmap(qrText, 600) }
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = "Reservation QR code",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                } else {
                    Text(
                        text = qrText,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TagCancelledText
                    )
                }
            }
        }
    )
}

fun generateQrBitmap(content: String, size: Int = 512): ImageBitmap? {
    return try {
        val writer = com.google.zxing.qrcode.QRCodeWriter()
        val bitMatrix = writer.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, size, size)
        val bmp = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp.asImageBitmap()
    } catch (t: Throwable) {
        null
    }
}

data class ReservationUi(
    val id: String,
    val station: String,
    val date: String,
    val timeRange: String,
    val status: ReservationStatus,
    val qrCode: String? = null
)

enum class ReservationStatus { Confirmed, Pending, Completed, Cancelled }