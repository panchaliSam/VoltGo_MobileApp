package lk.voltgo.voltgo.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen(
    onBackClick: () -> Unit,
    onViewDetails: (ReservationUi) -> Unit,
    onCancelReservation: (ReservationUi) -> Unit
) {
    val reservations by remember {
        mutableStateOf(
            listOf(
                ReservationUi("#R-1001", "Charge+ Union Place", "2025-10-08", "09:00–10:00", ReservationStatus.Confirmed),
                ReservationUi("#R-1002", "VoltGo – Kandy City Centre", "2025-10-09", "14:00–15:00", ReservationStatus.Pending),
                ReservationUi("#R-1003", "GreenCharge – Bambalapitiya", "2025-10-05", "18:30–19:30", ReservationStatus.Completed),
                ReservationUi("#R-1004", "EVHub – Nugegoda", "2025-10-11", "11:00–12:00", ReservationStatus.Confirmed),
                ReservationUi("#R-1005", "RapidEV – Malabe", "2025-10-03", "08:00–09:00", ReservationStatus.Cancelled),
            )
        )
    }

    val listState = rememberLazyListState()

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Reservations",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.DeepNavy,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AppColors.DeepNavy)
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
            if (reservations.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reservations, key = { it.id }) { res ->
                        ReservationCard(
                            res = res,
                            onClick = { onViewDetails(res) },
                            onCancel = { onCancelReservation(res) }
                        )
                    }
                }
            }
        }
    }
}

/* ---------- Data & UI helpers ---------- */

data class ReservationUi(
    val id: String,
    val station: String,
    val date: String,
    val timeRange: String,
    val status: ReservationStatus
)

enum class ReservationStatus { Confirmed, Pending, Completed, Cancelled }

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
            fontWeight = FontWeight.SemiBold,
            color = AppColors.DeepNavy
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "When you book a charging slot, it’ll appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.ElectricBlue
        )
    }
}

@Composable
fun StatusChip(status: ReservationStatus, shape: Shape = CircleShape) {
    // map to unique tag colors
    val (bg, text, border) = when (status) {
        ReservationStatus.Confirmed -> Triple(AppColors.TagConfirmedBg, AppColors.TagConfirmedText, null)
        ReservationStatus.Pending   -> Triple(AppColors.TagPendingBg, AppColors.TagPendingText, null)
        ReservationStatus.Completed -> Triple(AppColors.TagCompletedBg, AppColors.TagCompletedText, null)
        ReservationStatus.Cancelled -> Triple(AppColors.TagCancelledBg, AppColors.TagCancelledText, AppColors.TagCancelledBorder)
    }

    Box(
        modifier = Modifier
            .clip(shape)
            .then(if (border != null) Modifier.border(BorderStroke(1.dp, border), shape) else Modifier)
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelMedium,
            color = text,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun ReservationCard(
    res: ReservationUi,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    val canCancel = res.status == ReservationStatus.Confirmed || res.status == ReservationStatus.Pending
    var menuOpen by remember { mutableStateOf(false) }

    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)
    val cardShape = RoundedCornerShape(16.dp)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = gradientBorder,
                shape = cardShape
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = res.station,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.DeepNavy,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = AppColors.ElectricBlue)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("View details") },
                            onClick = {
                                menuOpen = false
                                onClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cancel reservation") },
                            onClick = {
                                menuOpen = false
                                onCancel()
                            },
                            enabled = canCancel
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = res.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.ElectricBlue,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = res.timeRange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.DeepNavy,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = res.id,
                    style = MaterialTheme.typography.labelLarge,
                    color = AppColors.ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(status = res.status)
            }
        }
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, widthDp = 420)
@Composable
private fun MyReservationsScreenPreview() {
    MaterialTheme {
        MyReservationsScreen(
            onBackClick = {},
            onViewDetails = {},
            onCancelReservation = {}
        )
    }
}
