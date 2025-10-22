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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.operator.OperatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVOperatorScreen(
    stationName: String,
    onBackClick: () -> Unit,
    viewModel: OperatorViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            // base64 encode the raw QR content
            val base64 = Base64.encodeToString(result.contents.toByteArray(), Base64.NO_WRAP)
            viewModel.verifyBase64(base64)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Station Operator") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
            Text(
                stationName,
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.DeepNavy,
                fontWeight = FontWeight.Bold
            )

            // Scan card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Brush.linearGradient(AppColors.logoBorderGradient), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Scan QR to verify booking", color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)

                    Button(
                        onClick = {
                            val opts = ScanOptions()
                                .setBeepEnabled(true)
                                .setPrompt("Align QR inside the frame")
                                .setOrientationLocked(false)
                            scanLauncher.launch(opts)
                        },
                        enabled = !ui.isVerifying,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (ui.isVerifying) "Verifying…" else "Scan QR")
                    }

                    if (ui.error != null) {
                        Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                    }
                    if (ui.info != null) {
                        Text(ui.info!!, color = AppColors.ElectricBlue)
                    }
                }
            }

            // Booking details card
            if (ui.booking != null) {
                val b = ui.booking!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, Brush.linearGradient(AppColors.logoBorderGradient), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Reservation", color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)
                        KeyValue("Booking ID", b.id)
                        KeyValue("Owner NIC", b.ownerNIC)
                        KeyValue("Station", b.stationId)
                        KeyValue("Slot", b.slotId ?: "-")
                        KeyValue("Date", b.reservationDate)
                        StatusPill(b.status)

                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { viewModel.complete() },
                                enabled = !ui.isCompleting && b.status.equals("Confirmed", true),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.ElectricBlue)
                            ) {
                                Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (ui.isCompleting) "Completing…" else "Complete booking")
                            }
                            OutlinedButton(
                                onClick = { viewModel.reset() },
                                shape = RoundedCornerShape(14.dp)
                            ) { Text("Scan another") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyValue(k: String, v: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(k, color = AppColors.DeepNavy.copy(alpha = .7f))
        Text(v, color = AppColors.DeepNavy, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatusPill(status: String) {
    val (bg, fg) = when (status.lowercase()) {
        "confirmed" -> AppColors.TagConfirmedBg to AppColors.TagConfirmedText
        "completed" -> AppColors.TagCompletedBg to AppColors.TagCompletedText
        "cancelled" -> AppColors.TagCancelledBg to AppColors.TagCancelledText
        else        -> AppColors.TagPendingBg to AppColors.TagPendingText
    }
    Surface(
        color = bg, contentColor = fg,
        shape = RoundedCornerShape(999.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(status, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontWeight = FontWeight.Medium)
    }
}