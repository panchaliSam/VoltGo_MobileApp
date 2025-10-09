package lk.voltgo.voltgo.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lk.voltgo.voltgo.ui.components.GradientActionCard
import lk.voltgo.voltgo.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMyReservationsClick: () -> Unit,
    onNewReservationClick: () -> Unit,
    onFindStationsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VoltGo ⚡",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            brush = Brush.horizontalGradient(AppColors.splashGradient)
                        ),
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { showProfileMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile"
                            )
                        }
                        DropdownMenu(
                            expanded = showProfileMenu,
                            onDismissRequest = { showProfileMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Profile") },
                                onClick = {
                                    showProfileMenu = false
                                    onEditProfileClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showProfileMenu = false
                                    onLogoutClick()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting / header
            Text(
                text = "Welcome back 👋",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "What would you like to do today?",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TranslucentWhite85
            )

            Spacer(Modifier.height(8.dp))

            // Cards
            GradientActionCard(
                title = "My Reservations",
                subtitle = "View, manage, or cancel your slots",
                icon = Icons.Filled.Bookmarks,
                onClick = onMyReservationsClick,
                modifier = Modifier.fillMaxWidth()
            )

            GradientActionCard(
                title = "Create Reservation",
                subtitle = "Book a charging slot now",
                icon = Icons.Filled.Edit,
                onClick = onNewReservationClick,
                modifier = Modifier.fillMaxWidth()
            )

            GradientActionCard(
                title = "Find Stations",
                subtitle = "Discover nearby chargers & availability",
                icon = Icons.Filled.Map,
                onClick = onFindStationsClick,
                modifier = Modifier.fillMaxWidth()
            )

            // Add more actions later if needed...
            // Add more actions later if needed...
        }
    }
}

@Composable
private fun AssistChipRow(
    onFindStationsClick: () -> Unit,
    onMyReservationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = onMyReservationsClick,
            label = { Text("Create Reservation") }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        onMyReservationsClick = {},
        onNewReservationClick = {},
        onFindStationsClick = {},
        onEditProfileClick = {},
        onLogoutClick = {}
    )
}
