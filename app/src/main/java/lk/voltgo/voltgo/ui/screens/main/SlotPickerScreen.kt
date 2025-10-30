// File: SlotPickerScreen.kt
// Author: Panchali Samarasinghe
// Date: 24 Oct 2025
// Version: 2.2

package lk.voltgo.voltgo.ui.screens.main

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.SlotDetailViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotPickerScreen(
    viewModel: SlotDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onConfirm: (stationId: String, slotNumber: Int, reservationDate: String, startTime: String, endTime: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedSlot by remember { mutableStateOf<Int?>(null) }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    val dateDialog = rememberMaterialDialogState()
    val timeDialog = rememberMaterialDialogState()
    val endTimeDialog = rememberMaterialDialogState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pick Reservation Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                uiState.error != null -> Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )

                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // --- Live Summary ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                            border = BorderStroke(1.dp, AppColors.ElectricBlue),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Reservation Summary", fontWeight = FontWeight.SemiBold)
                                Divider(Modifier.padding(vertical = 4.dp))
                                Text("Date: ${selectedDate?.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")) ?: "-"}")
                                Text("Slot: ${selectedSlot?.toString() ?: "-"}")
                                Text("Start: ${startTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) ?: "-"}")
                                Text("End: ${endTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) ?: "-"}")
                            }
                        }

                        // --- Date Picker ---
                        Text("Reservation Date", fontWeight = FontWeight.SemiBold)
                        OutlinedButton(
                            onClick = { dateDialog.show() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                selectedDate?.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
                                    ?: "Choose a date"
                            )
                        }

                        // --- Slot Selector ---
                        Text("Available Physical Slots", fontWeight = FontWeight.SemiBold)
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(180.dp)
                        ) {
                            items(uiState.physicalSlots.filter { it.isActive }) { slot ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (slot.number == selectedSlot)
                                                AppColors.ElectricBlue.copy(alpha = 0.1f)
                                            else AppColors.BrandWhite
                                        )
                                        .border(
                                            width = if (slot.number == selectedSlot) 2.dp else 1.5.dp,
                                            brush = Brush.linearGradient(AppColors.logoBorderGradient),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedSlot = slot.number }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Slot ${slot.number}", fontWeight = FontWeight.SemiBold)
                                        slot.label?.let {
                                            Text(it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }

                        // --- Time Pickers ---
                        Text("Select Time", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { timeDialog.show() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(startTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) ?: "Start Time")
                            }
                            OutlinedButton(
                                onClick = { endTimeDialog.show() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(endTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) ?: "End Time")
                            }
                        }

                        // --- Confirm Button ---
                        Button(
                            onClick = {
                                if (selectedDate == null || selectedSlot == null || startTime == null || endTime == null) {
                                    Toast.makeText(context, "Please complete all selections", Toast.LENGTH_SHORT).show()
                                } else {
                                    val stationId = uiState.station?.id ?: return@Button
                                    val reservationDateUtc = selectedDate!!.atStartOfDay().atOffset(ZoneOffset.UTC).toString()
                                    val startTimeUtc = selectedDate!!.atTime(startTime!!).atOffset(ZoneOffset.UTC).toString()
                                    val endTimeUtc = selectedDate!!.atTime(endTime!!).atOffset(ZoneOffset.UTC).toString()
                                    onConfirm(stationId, selectedSlot!!, reservationDateUtc, startTimeUtc, endTimeUtc)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.ElectricBlue)
                        ) {
                            Text("Next", fontWeight = FontWeight.Medium)
                        }
                    }

                    // --- Date Picker Dialog ---
                    MaterialDialog(
                        dialogState = dateDialog,
                        buttons = {
                            positiveButton("OK")
                            negativeButton("Cancel")
                        }
                    ) {
                        datepicker(
                            initialDate = LocalDate.now(),
                            title = "Pick a date"
                        ) { selectedDate = it }
                    }

                    // --- Start Time Picker Dialog ---
                    MaterialDialog(
                        dialogState = timeDialog,
                        buttons = {
                            positiveButton("OK")
                            negativeButton("Cancel")
                        }
                    ) {
                        timepicker(title = "Pick start time", is24HourClock = false) {
                            startTime = it
                        }
                    }

                    // --- End Time Picker Dialog ---
                    MaterialDialog(
                        dialogState = endTimeDialog,
                        buttons = {
                            positiveButton("OK")
                            negativeButton("Cancel")
                        }
                    ) {
                        timepicker(title = "Pick end time", is24HourClock = false) {
                            endTime = it
                        }
                    }
                }
            }
        }
    }
}