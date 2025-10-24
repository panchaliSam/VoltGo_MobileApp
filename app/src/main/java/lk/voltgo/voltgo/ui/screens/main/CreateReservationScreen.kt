// File: CreateReservationScreen.kt
// Author: Panchali Samarasinghe
// Date: 24 Oct 2025
// Version: 2.0 - Updated to accept route arguments

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.CreateReservationViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    onBackClick: () -> Unit,
    onSuccess: (bookingId: String) -> Unit,
    viewModel: CreateReservationViewModel = hiltViewModel(),
    navBackStackEntry: NavBackStackEntry? = null
) {
    val context = LocalContext.current
    val args = navBackStackEntry?.arguments

    // Extract route arguments
    val stationId = args?.getString("stationId").orEmpty()
    val physicalSlotNumber = args?.getString("physicalSlotNumber")?.toIntOrNull() ?: 0
    val reservationDateIso = args?.getString("reservationDateIso").orEmpty()
    val startTimeIso = args?.getString("startTimeIso").orEmpty()
    val endTimeIso = args?.getString("endTimeIso").orEmpty()

    // Initialize ViewModel only once
    LaunchedEffect(Unit) {
        viewModel.initialize(
            stationId = stationId,
            physicalSlotNumber = physicalSlotNumber,
            reservationDateIso = reservationDateIso,
            startTimeIso = startTimeIso,
            endTimeIso = endTimeIso
        )
    }

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
                    "Reservation Summary",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.DeepNavy,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                InfoRow("Station ID", ui.stationId)
                InfoRow("Physical Slot", ui.physicalSlotNumber.toString())
                InfoRow("Date", formatDate(ui.reservationDateIso))
                InfoRow("Start Time", formatSingleTime(ui.startTimeIso))
                InfoRow("End Time", formatSingleTime(ui.endTimeIso))
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
    OffsetDateTime.parse(iso).format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
} catch (_: Exception) { iso }

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeRange(startIso: String, endIso: String): String = try {
    val start = OffsetDateTime.parse(startIso).format(DateTimeFormatter.ofPattern("hh:mm a"))
    val end = OffsetDateTime.parse(endIso).format(DateTimeFormatter.ofPattern("hh:mm a"))
    "$start – $end"
} catch (_: Exception) { "-" }

@RequiresApi(Build.VERSION_CODES.O)
private fun formatSingleTime(iso: String): String = try {
    OffsetDateTime.parse(iso).format(DateTimeFormatter.ofPattern("hh:mm a"))
} catch (_: Exception) { "-" }