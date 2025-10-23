// EVOperatorScreen.kt
package lk.voltgo.voltgo.ui.screens.operator

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Logout
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
import lk.voltgo.voltgo.ui.viewmodel.operator.OperatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVOperatorScreen(
    stationName: String,
    onLoggedOut: () -> Unit,
    viewModel: OperatorViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(ui.loggedOut) {
        if (ui.loggedOut) onLoggedOut()
    }

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val base64 = Base64.encodeToString(result.contents.toByteArray(), Base64.NO_WRAP)
            viewModel.verifyBase64(base64)
        }
    }

    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Station Operator",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.DeepNavy,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Logout action (DeepNavy tint to match your palette)
                    IconButton(
                        onClick = { viewModel.logout() },
                        enabled = !ui.isLoggingOut
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = "Logout",
                            tint = AppColors.DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = AppColors.BrandWhite
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(AppColors.BrandWhite)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header card (mirrors MyReservations header)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, gradientBorder, cardShape),
                shape = cardShape,
                colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        stationName,
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.DeepNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Scan the customer’s QR, verify booking, then complete.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.ElectricBlue
                    )
                }
            }

            // Scan card (same gradient border + colors)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, gradientBorder, cardShape),
                shape = cardShape,
                colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Scan QR to verify booking", color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)

                    VoltGoGradientButton(
                        text = if (ui.isVerifying) "Verifying…" else "Scan QR",
                        leadingIcon = Icons.Default.QrCodeScanner,
                        enabled = !ui.isVerifying,
                        onClick = {
                            val opts = ScanOptions()
                                .setBeepEnabled(true)
                                .setPrompt("Align QR inside the frame")
                                .setOrientationLocked(false)
                            scanLauncher.launch(opts)
                        }
                    )

                    if (ui.error != null) {
                        Text(ui.error!!, color = AppColors.TagCancelledText, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (ui.info != null) {
                        Text(ui.info!!, color = AppColors.ElectricBlue, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Booking details card (colors & chip style match MyReservations)
            if (ui.booking != null) {
                val b = ui.booking!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, gradientBorder, cardShape),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Reservation",
                                color = AppColors.DeepNavy,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f)
                            )
                            StatusChip(status = b.status)
                        }

                        KeyValue("Booking ID", b.id)
                        KeyValue("Owner NIC", b.ownerNIC)
                        KeyValue("Station", b.stationId)
                        KeyValue("Slot", b.slotId ?: "-")
                        KeyValue("Date", b.reservationDate)

                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            VoltGoGradientButton(
                                text = if (ui.isCompleting) "Completing…" else "Complete booking",
                                leadingIcon = Icons.Outlined.CheckCircle,
                                enabled = !ui.isCompleting && b.status.equals("Confirmed", true),
                                onClick = { viewModel.complete(b.id) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedButton(
                                onClick = { viewModel.reset() },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.DeepNavy)
                            ) { Text("Scan another") }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Shared bits styled like MyReservations ---------- */

@Composable
private fun KeyValue(k: String, v: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(k, color = AppColors.DeepNavy.copy(alpha = .7f), style = MaterialTheme.typography.bodyMedium)
        Text(v, color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StatusChip(status: String) {
    val (label, bg, fg) = when (status.lowercase()) {
        "confirmed" -> Triple("Confirmed", AppColors.TagConfirmedBg, AppColors.TagConfirmedText)
        "pending"   -> Triple("Pending",   AppColors.TagPendingBg,   AppColors.TagPendingText)
        "completed" -> Triple("Completed", AppColors.TagCompletedBg, AppColors.TagCompletedText)
        "cancelled" -> Triple("Cancelled", AppColors.TagCancelledBg, AppColors.TagCancelledText)
        else        -> Triple(status,      AppColors.TagPendingBg,   AppColors.TagPendingText)
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        contentColor = fg,
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1
        )
    }
}