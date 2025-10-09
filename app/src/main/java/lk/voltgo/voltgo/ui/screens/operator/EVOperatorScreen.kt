package lk.voltgo.voltgo.ui.screens.operator

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.screens.main.ReservationStatus
import lk.voltgo.voltgo.ui.screens.main.ReservationUi
import lk.voltgo.voltgo.ui.screens.main.StatusChip
import lk.voltgo.voltgo.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EVOperatorScreen(
    stationName: String,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onViewReservation: (ReservationUi) -> Unit,
    onScanQrFor: (ReservationUi) -> Unit,
    onQuickScan: () -> Unit
) {
    // TODO: replace with VM-provided state
    val reservations by remember {
        mutableStateOf(
            listOf(
                ReservationUi("#R-2001", stationName, "2025-10-09", "10:00–11:00", ReservationStatus.Confirmed),
                ReservationUi("#R-2002", stationName, "2025-10-09", "11:00–12:00", ReservationStatus.Pending),
                ReservationUi("#R-1999", stationName, "2025-10-08", "18:00–19:00", ReservationStatus.Completed),
                ReservationUi("#R-1998", stationName, "2025-10-08", "19:00–20:00", ReservationStatus.Cancelled)
            )
        )
    }

    val listState = rememberLazyListState()
    val cardShape = RoundedCornerShape(16.dp)
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Operator — $stationName",
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
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = AppColors.DeepNavy)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onQuickScan,
                containerColor = AppColors.ElectricBlue,
                contentColor = AppColors.BrandWhite,
            ) {
                Icon(Icons.Outlined.QrCodeScanner, contentDescription = "Scan any QR")
                Spacer(Modifier.width(8.dp))
                Text("Quick Scan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BrandWhite)
                .padding(padding)
        ) {
            if (reservations.isEmpty()) {
                OperatorEmptyState()
            } else {
                // header card with little summary
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
                            "Today’s Reservations",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.DeepNavy,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Scan a customer’s QR code to validate and start the session.",
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
                    items(reservations, key = { it.id }) { res ->
                        OperatorReservationCard(
                            res = res,
                            onView = { onViewReservation(res) },
                            onScan = { onScanQrFor(res) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) } // leave space for FAB
                }
            }
        }
    }
}

@Composable
private fun OperatorReservationCard(
    res: ReservationUi,
    onView: () -> Unit,
    onScan: () -> Unit
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
            // Top row: Reservation ID + status
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = res.id,
                    style = MaterialTheme.typography.titleSmall,
                    color = AppColors.ElectricBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                StatusChip(status = res.status)
            }

            Spacer(Modifier.height(6.dp))

            // Date/time
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = res.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = res.timeRange,
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
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.DeepNavy)
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("View")
                }
                Button(
                    onClick = onScan,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.ElectricBlue,
                        contentColor = AppColors.BrandWhite
                    )
                ) {
                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Scan QR")
                }
            }
        }
    }
}

@Composable
private fun OperatorEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "No reservations for this station",
            style = MaterialTheme.typography.titleMedium,
            color = AppColors.DeepNavy,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "New bookings will appear here. You can still use Quick Scan to validate a walk-in.",
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.ElectricBlue
        )
    }
}

/* ---------- Preview ---------- */
@Preview(showBackground = true, widthDp = 420)
@Composable
private fun EVOperatorScreenPreview() {
    MaterialTheme {
        EVOperatorScreen(
            stationName = "VoltGo – Kandy City Centre",
            onBackClick = {},
            onRefresh = {},
            onViewReservation = {},
            onScanQrFor = {},
            onQuickScan = {}
        )
    }
}