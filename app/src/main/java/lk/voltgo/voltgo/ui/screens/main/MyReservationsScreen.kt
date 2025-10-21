/**
 * ------------------------------------------------------------
 * File: MyReservationsScreen.kt
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.2
 *
 * Change log:
 *  - Match UI style of EVOperatorScreen (cards, gradient borders, status chip, buttons)
 *  - Keep filters, loading/error/empty states
 *  - Show QR for Confirmed and Completed (if qrCode != null)
 * ------------------------------------------------------------
 */
package lk.voltgo.voltgo.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.components.VoltGoDangerOutlinedButton
import lk.voltgo.voltgo.ui.components.VoltGoFilterPill
import lk.voltgo.voltgo.ui.components.VoltGoGradientButton
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

    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Reservations",
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
                    // Optional header card (mirrors operator header style)
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
                                "Your Reservations",
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.DeepNavy,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "View, cancel, or show the QR to station operators.",
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
                        items(state.filtered, key = { it.id }) { res ->
                            MyReservationCard(
                                res = res,
                                onView = { onViewDetails(res) },
                                onShowQr = {
                                    if (!res.qrCode.isNullOrBlank()) qrToShow = res.qrCode
                                },
                                onCancel = { onCancelReservation(res) }
                            )
                        }
                        item { Spacer(Modifier.height(24.dp)) }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            VoltGoFilterPill(
                text = item.name,
                selected = item == active,
                onClick = { onSelect(item) }
            )
        }
    }
}

/* ---------- Status chip (styled like EVOperatorScreen) ---------- */

@Composable
private fun StatusChip(status: ReservationStatus) {
    val (label, bg, fg) = when (status) {
        ReservationStatus.Confirmed -> Triple(
            "Confirmed",
            AppColors.TagConfirmedBg,
            AppColors.TagConfirmedText
        )
        ReservationStatus.Pending -> Triple(
            "Pending",
            AppColors.TagPendingBg,
            AppColors.TagPendingText
        )
        ReservationStatus.Completed -> Triple(
            "Completed",
            AppColors.TagCompletedBg,
            AppColors.TagCompletedText
        )
        ReservationStatus.Cancelled -> Triple(
            "Cancelled",
            AppColors.TagCancelledBg,
            AppColors.TagCancelledText
        )
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        contentColor = fg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.35f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
    }
}

/* ---------- Item card (mirrors OperatorReservationCard styling) ---------- */

@Composable
private fun MyReservationCard(
    res: ReservationUi,
    onView: () -> Unit,
    onShowQr: () -> Unit,
    onCancel: () -> Unit
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
            // Top row: Station name + status
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

            // ID + Date/Time
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

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val canShowQr = (res.status == ReservationStatus.Confirmed || res.status == ReservationStatus.Completed) &&
                        !res.qrCode.isNullOrBlank()

                if (canShowQr) {
                    VoltGoGradientButton(
                        text = "Show QR",
                        leadingIcon = Icons.Outlined.QrCodeScanner,
                        onClick = onShowQr,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (res.status == ReservationStatus.Pending || res.status == ReservationStatus.Confirmed) {
                    VoltGoDangerOutlinedButton(
                        text = "Cancel",
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/* ---------- Error/Empty states (colors preserved) ---------- */

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
        VoltGoGradientButton(
            text = "Retry",
            onClick = onRetry
        )
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
            color = AppColors.DeepNavy,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "When you make a reservation, it will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.ElectricBlue
        )
    }
}

/* ---------- QR dialog & generator (unchanged, colors preserved) ---------- */

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

/* ---------- Models ---------- */

data class ReservationUi(
    val id: String,
    val station: String,
    val date: String,
    val timeRange: String,
    val status: ReservationStatus,
    val qrCode: String? = null
)

enum class ReservationStatus { Confirmed, Pending, Completed, Cancelled }
