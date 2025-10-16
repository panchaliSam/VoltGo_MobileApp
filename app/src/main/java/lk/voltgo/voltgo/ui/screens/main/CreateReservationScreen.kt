/**
 * ------------------------------------------------------------
 * File: CreateReservationScreen.kt
 * Author: Panchali Samarasinghe
 * Created: October 10, 2025
 * Version: 1.0
 *
 * Description:
 * This file defines the UI and logic for creating a new EV charging reservation
 * in the VoltGo app. It includes a form with station selection, date/time pickers,
 * connector type selection, and notes, along with form validation and submission.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.theme.AppColors

/* ---------- Public API ---------- */

// Data model representing all fields required for creating a new reservation.
data class ReservationForm(
    val station: String,
    val date: String,       // e.g., 2025-10-12 (plug real date picker later)
    val startTime: String,  // e.g., 09:00
    val endTime: String,    // e.g., 10:00
    val connectorType: String,
    val powerKw: String?,   // optional
    val notes: String?
)

@OptIn(ExperimentalMaterial3Api::class)
// Main composable that renders the Create Reservation screen UI.
// Includes input fields, dropdowns, and form validation before submission.
@Composable
fun CreateReservationScreen(
    onBackClick: () -> Unit,
    onOpenMap: () -> Unit,             // optional action to pick station from map
    onSubmit: (ReservationForm) -> Unit
) {
    val scroll = rememberScrollState()

    // mock options (replace with VM-provided lists)
    val stationOptions = listOf(
        "Charge+ Union Place",
        "VoltGo – Kandy City Centre",
        "GreenCharge – Bambalapitiya",
        "EVHub – Nugegoda",
        "RapidEV – Malabe"
    )
    val connectorOptions = listOf("CCS2", "CHAdeMO", "Type 2 (AC)", "GB/T")

    // form state
    var station by remember { mutableStateOf("") }
    var stationExpanded by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    var connector by remember { mutableStateOf("") }
    var connectorExpanded by remember { mutableStateOf(false) }

    var power by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // simple validation
    val isValid = station.isNotBlank() &&
            date.matches(Regex("""\d{4}-\d{2}-\d{2}""")) &&
            startTime.matches(Regex("""\d{2}:\d{2}""")) &&
            endTime.matches(Regex("""\d{2}:\d{2}""")) &&
            connector.isNotBlank()

    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Reservation",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BrandWhite)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card wrapper: mirrors MyReservationsScreen style
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, brush = gradientBorder, shape = cardShape),
                shape = cardShape,
                colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Reservation Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppColors.DeepNavy,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Station (dropdown)
                    ExposedDropdownMenuBox(
                        expanded = stationExpanded,
                        onExpandedChange = { stationExpanded = !stationExpanded }
                    ) {
                        OutlinedTextField(
                            value = station,
                            onValueChange = { station = it },
                            label = { Text("Station") },
                            leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = AppColors.ElectricBlue) },
                            trailingIcon = { TrailingIcon(expanded = stationExpanded) },
                            singleLine = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ElectricBlue,
                                cursorColor = AppColors.ElectricBlue
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = stationExpanded,
                            onDismissRequest = { stationExpanded = false }
                        ) {
                            stationOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        station = option
                                        stationExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            leadingIcon = { Icon(Icons.Filled.CalendarToday, null, tint = AppColors.ElectricBlue) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ElectricBlue,
                                cursorColor = AppColors.ElectricBlue
                            )
                        )
                        IconButton(
                            onClick = { /* open your date picker */ },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(48.dp)
                        ) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date", tint = AppColors.DeepNavy)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start (HH:MM)") },
                            leadingIcon = { Icon(Icons.Filled.Schedule, null, tint = AppColors.ElectricBlue) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ElectricBlue,
                                cursorColor = AppColors.ElectricBlue
                            )
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("End (HH:MM)") },
                            leadingIcon = { Icon(Icons.Filled.Schedule, null, tint = AppColors.ElectricBlue) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ElectricBlue,
                                cursorColor = AppColors.ElectricBlue
                            )
                        )
                    }

                    // Connector type (dropdown)
                    ExposedDropdownMenuBox(
                        expanded = connectorExpanded,
                        onExpandedChange = { connectorExpanded = !connectorExpanded }
                    ) {
                        OutlinedTextField(
                            value = connector,
                            onValueChange = { connector = it },
                            label = { Text("Connector Type") },
                            leadingIcon = { Icon(Icons.Filled.Power, contentDescription = null, tint = AppColors.ElectricBlue) },
                            trailingIcon = { TrailingIcon(expanded = connectorExpanded) },
                            singleLine = true,
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ElectricBlue,
                                cursorColor = AppColors.ElectricBlue
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = connectorExpanded,
                            onDismissRequest = { connectorExpanded = false }
                        ) {
                            connectorOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        connector = option
                                        connectorExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = power,
                            onValueChange = { power = it },
                            label = { Text("Power (kW) — optional") },
                            leadingIcon = { Icon(Icons.Filled.Power, null, tint = AppColors.ElectricBlue) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppColors.ElectricBlue,
                                cursorColor = AppColors.ElectricBlue
                            )
                        )

                        OutlinedButton(
                            onClick = onOpenMap,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .height(56.dp)
                                .weight(1f),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.DeepNavy)
                        ) {
                            Icon(Icons.Filled.Map, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Pick on Map")
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        leadingIcon = { Icon(Icons.Filled.Notes, null, tint = AppColors.ElectricBlue) },
                        singleLine = false,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { /* hide kb if needed */ }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.ElectricBlue,
                            cursorColor = AppColors.ElectricBlue
                        )
                    )
                }
            }

            // Submit bar
            Button(
                onClick = {
                    onSubmit(
                        ReservationForm(
                            station = station.trim(),
                            date = date.trim(),
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            connectorType = connector.trim(),
                            powerKw = power.trim().ifBlank { null },
                            notes = notes.trim().ifBlank { null }
                        )
                    )
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.ElectricBlue,
                    contentColor = AppColors.BrandWhite
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Create Reservation", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }

            if (!isValid) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            "Required: Station, Date (YYYY-MM-DD), Start & End (HH:MM), Connector",
                            color = AppColors.TagPendingText
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = AppColors.TagPendingBg,
                        labelColor = AppColors.TagPendingText
                    )
                )
            }
        }
    }
}

/* ---------- Preview ---------- */

// Preview function for displaying the Create Reservation screen in Android Studio's preview mode.
@Preview(showBackground = true, widthDp = 420)
@Composable
private fun CreateReservationPreview() {
    MaterialTheme {
        CreateReservationScreen(
            onBackClick = {},
            onOpenMap = {},
            onSubmit = {}
        )
    }
}