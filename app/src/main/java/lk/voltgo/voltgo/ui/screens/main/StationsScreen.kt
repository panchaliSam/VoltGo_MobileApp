/**
 * ------------------------------------------------------------
 * File: StationsScreen.kt
 * Author: Ishini Aposo
 * Created: October 10, 2025
 * Version: 1.0
 *
 * Description:
 * This file defines the Stations screen of the VoltGo app.
 * It allows users to view available EV charging stations on a map,
 * filter stations using a search bar, and view station summary cards.
 * The screen integrates Google Maps and displays station markers dynamically.
 * ------------------------------------------------------------
 */

package lk.voltgo.voltgo.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.StationViewModel

// Main composable for the Stations Screen.
// Displays a map with EV charging stations, search functionality,
// and summary cards for pending and approved stations.
@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
    viewModel: StationViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val selectedStation = remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.8731, 80.7718), 7.5f)
    }

    // Handle search (unchanged)
    val stations = uiState.stations
    val filteredStations = remember(searchQuery, stations) {
        if (searchQuery.isBlank()) stations
        else stations.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Focus camera when only one station matches search (unchanged)
    LaunchedEffect(filteredStations.size) {
        if (filteredStations.size == 1) {
            val st = filteredStations.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(st.latitude, st.longitude), 13f
            )
            selectedStation.value = LatLng(st.latitude, st.longitude)
        }
    }

    Scaffold(
        containerColor = AppColors.BrandWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Stations",
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.DeepNavy,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.DeepNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.BrandWhite) // same bg as MyReservations
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Summary row -> styled like ReservationCard (gradient border, white container)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StationSummaryCard(label = "Pending", count = 2)
                StationSummaryCard(label = "Approved", count = 5)
            }

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = true),
                        properties = MapProperties(isMyLocationEnabled = false)
                    ) {
                        filteredStations.forEach { st ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(st.latitude, st.longitude)
                                ),
                                title = st.name,
                                snippet = st.type
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Reused/updated UI pieces to match MyReservations look ---------- */

// Reusable UI component that displays a gradient-bordered summary card
// showing the number of stations for a specific status (e.g., Pending, Approved).
@Composable
private fun RowScope.StationSummaryCard(
    label: String,
    count: Int
) {
    val gradientBorder = Brush.linearGradient(AppColors.splashGradient)
    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .border(width = 2.dp, brush = gradientBorder, shape = shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = AppColors.BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DeepNavy
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(color = AppColors.ElectricBlue),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Search bar component for filtering stations by name.
// Accepts the current query and a callback to handle query changes.
@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    // functionally identical; light style nudge to fit white bg
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AppColors.TranslucentWhite20, // subtle, as before
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textStyle = LocalTextStyle.current.copy(color = AppColors.DeepNavy),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text("Search station...", color = AppColors.ElectricBlue)
                }
                innerTextField()
            }
        )
    }
}

/* ---------- Preview ---------- */
// Preview function for displaying the Stations Screen layout in Android Studio’s preview mode.
@Preview(showBackground = true, widthDp = 420)
@Composable
fun StationsScreenPreview() {
    MaterialTheme {
        StationsScreen(onBackClick = {})
    }
}
