package lk.voltgo.voltgo.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import lk.voltgo.voltgo.ui.viewmodel.main.SlotPickerViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SlotPickerScreen(
    viewModel: SlotPickerViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onSelect: (stationId: String, slotId: String, reservationDateIso: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Select a time slot") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                    }
                }
                else -> {
                    val grouped = remember(uiState.slots) { uiState.slots.groupBy { dateKey(it.reservationDate) } }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        grouped.forEach { (date, slots) ->
                            item {
                                DateHeader(date)
                            }
                            items(slots, key = { it.id }) { slot ->
                                SlotRow(
                                    start = timeShort(slot.startTime),
                                    end = timeShort(slot.endTime),
                                    description = slot.description ?: "",
                                    available = slot.isAvailable == true,
                                    onClick = {
                                        if (slot.isAvailable == true) {
                                            val dateIso = slot.reservationDate ?: slot.startTime ?: ""
                                            onSelect(uiState.stationId, slot.id, dateIso)
                                        }
                                    }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(12.dp)) }
                    }
                }
            }
        }
    }
}

/* ---------- UI bits ---------- */

@Composable
private fun DateHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = AppColors.DeepNavy,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SlotRow(
    start: String,
    end: String,
    description: String,
    available: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val borderBrush = Brush.linearGradient(AppColors.logoBorderGradient)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.BrandWhite)
            .border(1.25.dp, borderBrush, shape)
            .clickable(enabled = available) { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text("$start – $end", fontWeight = FontWeight.SemiBold, color = AppColors.DeepNavy)
            if (description.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(description, color = AppColors.DeepNavy.copy(alpha = 0.75f), style = MaterialTheme.typography.bodySmall)
            }
        }
        AvailabilityTag(available)
    }
}

@Composable
private fun AvailabilityTag(available: Boolean) {
    val (bg, fg, border, label) = if (available) {
        arrayOf(AppColors.TagCompletedBg, AppColors.TagCompletedText, AppColors.TagCompletedBorder, "Available")
    } else {
        arrayOf(AppColors.TagCancelledBg, AppColors.TagCancelledText, AppColors.TagCancelledBorder, "Unavailable")
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg as androidx.compose.ui.graphics.Color)
            .border(1.dp, border as androidx.compose.ui.graphics.Color, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(fg as androidx.compose.ui.graphics.Color)
        )
        Spacer(Modifier.width(6.dp))
        Text(label as String, color = fg as androidx.compose.ui.graphics.Color, style = MaterialTheme.typography.labelMedium)
    }
}

/* ---------- Time helpers (ISO8601 “Z”) ---------- */

@RequiresApi(Build.VERSION_CODES.O)
private fun dateKey(iso: String?): String {
    if (iso.isNullOrBlank()) return "-"
    val odt = OffsetDateTime.parse(iso)
    return odt.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
}

@RequiresApi(Build.VERSION_CODES.O)
private fun timeShort(iso: String?): String {
    if (iso.isNullOrBlank()) return "--:--"
    val odt = OffsetDateTime.parse(iso)
    return odt.format(DateTimeFormatter.ofPattern("HH:mm"))
}