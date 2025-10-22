package lk.voltgo.voltgo.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.CreateReservationViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateReservationScreen(
    viewModel: CreateReservationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSuccess: (bookingId: String) -> Unit
) {
    val ui by viewModel.ui.collectAsState()

    // If success → hand off bookingId
    ui.success?.let { success ->
        LaunchedEffect(success.bookingId) {
            onSuccess(success.bookingId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Confirm reservation") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary card
            GradientCard {
                Text(
                    "Summary",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.DeepNavy,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                InfoRow("Station ID", ui.stationId)
                InfoRow("Slot ID", ui.slotId)
                InfoRow("Date", formatDate(ui.reservationDateIso))
            }

            // Notes
            GradientCard {
                Text(
                    "Notes (optional)",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.DeepNavy,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = ui.notes,
                    onValueChange = viewModel::updateNotes,
                    placeholder = { Text("e.g., Tesla Model 3 charging session") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false,
                    minLines = 2
                )
            }

            Spacer(Modifier.weight(1f))

            // Error
            ui.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Submit
            Button(
                onClick = { viewModel.submit() },
                enabled = !ui.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.ElectricBlue)
            ) {
                if (ui.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = AppColors.BrandWhite
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Creating…", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Create reservation", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/* ---------- Helpers ---------- */

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

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(iso: String): String = try {
    val dt = OffsetDateTime.parse(iso)
    dt.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
} catch (_: Exception) { iso }

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeRange(iso: String): String {
    // If you only have the date (midnight), return "-"
    return try {
        val dt = OffsetDateTime.parse(iso)
        dt.format(DateTimeFormatter.ofPattern("HH:mm")) // adjust if you want a range
    } catch (_: Exception) { "-" }
}