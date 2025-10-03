package lk.voltgo.voltgo.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lk.chargehere.app.ui.theme.AppColors
import java.time.LocalDate
import java.time.LocalTime

data class Station(
    val id: String,
    val name: String,
    val type: String,    // "AC" or "DC"
    val location: String
)
data class Slot(val id: String, val label: String) // e.g., "Slot-01"

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FindStationScreen(
    onReservationConfirmed: (station: Station, slot: Slot, from: LocalTime, to: LocalTime, date: LocalDate) -> Unit = { _,_,_,_,_ -> }
) {
    // Mock data
    val stations = remember {
        listOf(
            Station("1", "SLIIT Malabe – DC Charging", "DC", "Malabe"),
            Station("2", "SLIIT Malabe – AC Charging", "AC", "Malabe"),
            Station("3", "Colombo City Center – DC", "DC", "Colombo 02"),
        )
    }
    val slots = remember { listOf("Slot-01", "Slot-02", "Slot-03").mapIndexed { i,s -> Slot("${i+1}", s) } }

    var selectedStation by remember { mutableStateOf<Station?>(null) }
    var selectedSlot by remember { mutableStateOf<Slot?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) } // tomorrow
    var fromTime by remember { mutableStateOf(LocalTime.of(9,0)) }
    var toTime by remember { mutableStateOf(LocalTime.of(10,0)) }

    Box(
        Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(AppColors.splashGradient))
            .padding(16.dp)
    ) {
        Column(Modifier.fillMaxSize()) {

            Text(
                "Find & Reserve",
                style = MaterialTheme.typography.headlineLarge,
                color = AppColors.TranslucentWhite90,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(16.dp))

            // Step 1: Stations
            Text("Stations", color = AppColors.TranslucentWhite85, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stations) { st ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStation = st
                                // Auto-preset DC flow to match your scenario
                                if (st.name.contains("SLIIT Malabe") && st.type == "DC") {
                                    selectedSlot = slots.firstOrNull { it.label == "Slot-01" }
                                    selectedDate = LocalDate.now().plusDays(1)
                                    fromTime = LocalTime.of(9, 0)
                                    toTime = LocalTime.of(10, 0)
                                }
                            }
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(st.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("${st.type} • ${st.location}", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (selectedStation?.id == st.id) {
                                AssistChip(onClick = {}, label = { Text("Selected") })
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Step 2: Slot / Time
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Reservation Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))

                    // Slot dropdown (simple buttons for now)
                    Text("Slots", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        slots.forEach { s ->
                            FilterChip(
                                selected = selectedSlot?.id == s.id,
                                onClick = { selectedSlot = s },
                                label = { Text(s.label) }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Date", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    // simple next/prev day controls
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { selectedDate = selectedDate.minusDays(1) }) { Text("− Day") }
                        AssistChip(onClick = {}, label = { Text(selectedDate.toString()) })
                        OutlinedButton(onClick = { selectedDate = selectedDate.plusDays(1) }) { Text("+ Day") }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Time", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    // simple presets to keep UX clean
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val presets = listOf(
                            LocalTime.of(9,0) to LocalTime.of(10,0),
                            LocalTime.of(10,0) to LocalTime.of(11,0),
                            LocalTime.of(11,0) to LocalTime.of(12,0)
                        )
                        presets.forEach { (f,t) ->
                            FilterChip(
                                selected = (fromTime == f && toTime == t),
                                onClick = { fromTime = f; toTime = t },
                                label = { Text("${f.toString()} – ${t.toString()}") }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    val canConfirm =
                        selectedStation != null && selectedSlot != null && fromTime.isBefore(toTime)
                    Button(
                        onClick = {
                            onReservationConfirmed(
                                selectedStation!!,
                                selectedSlot!!,
                                fromTime, toTime,
                                selectedDate
                            )
                        },
                        enabled = canConfirm,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.EnergyAmber)
                    ) {
                        Text("Confirm Reservation", color = AppColors.DeepNavy, fontWeight = FontWeight.Bold)
                    }

                    if (selectedStation != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Summary: ${selectedStation!!.name} • ${selectedSlot?.label ?: "—"} • $selectedDate • $fromTime – $toTime",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun FindStationPreview() {
    FindStationScreen()
}
