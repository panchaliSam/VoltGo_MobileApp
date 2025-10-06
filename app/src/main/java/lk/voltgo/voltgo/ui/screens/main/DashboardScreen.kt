package lk.voltgo.voltgo.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.*

import lk.chargehere.app.ui.theme.AppColors

@SuppressLint("MissingPermission")
@Composable
fun DashboardScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var showAllStations by remember { mutableStateOf(false) }

    val userLocation = LatLng(6.9271, 79.8612) // mock user location (Colombo)
    val allStations = listOf(
        Triple("SLIIT Malabe – DC", LatLng(6.9147, 79.9725), "ST001"),
        Triple("Colombo City Center – AC", LatLng(6.9271, 79.8612), "ST002"),
        Triple("Kandy EV Hub – DC", LatLng(7.2906, 80.6337), "ST003"),
        Triple("Galle Marine Station – AC", LatLng(6.0535, 80.2210), "ST004"),
        Triple("Negombo PowerHub – DC", LatLng(7.2008, 79.8737), "ST005")
    )

    // Filtered stations based on search or nearest logic
    val filteredStations = remember(searchQuery, showAllStations) {
        if (searchQuery.isNotEmpty()) {
            allStations.filter { it.first.contains(searchQuery, ignoreCase = true) }
        } else if (!showAllStations) {
            allStations.sortedBy { distance(userLocation, it.second) }.take(3)
        } else {
            allStations
        }
    }

    val sriLankaCenter = LatLng(7.8731, 80.7718)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            if (searchQuery.isEmpty()) userLocation else sriLankaCenter,
            if (showAllStations) 7.5f else 10f
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.ElectricBlue)
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = AppColors.BrandWhite,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Summary cards
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryCard("Pending", 2, AppColors.EnergyAmber)
            SummaryCard("Approved", 5, AppColors.BrandGreen)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onToggleView = { showAllStations = !showAllStations },
            showAll = showAllStations
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Google Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = true),
                properties = MapProperties(isMyLocationEnabled = false)
            ) {
                filteredStations.forEach { (name, location, _) ->
                    Marker(
                        state = MarkerState(position = location),
                        title = name,
                        snippet = "Available now"
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onToggleView: () -> Unit, showAll: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TranslucentWhite20, shape = MaterialTheme.shapes.medium)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            textStyle = LocalTextStyle.current.copy(color = AppColors.BrandWhite),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text("Search station...", color = AppColors.TranslucentWhite85)
                }
                innerTextField()
            }
        )
        TextButton(onClick = onToggleView) {
            Text(
                if (showAll) "Nearest" else "Show All",
                color = AppColors.BrandBlue
            )
        }
    }
}

@Composable
fun SummaryCard(label: String, count: Int, color: Color) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
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
                style = MaterialTheme.typography.bodyLarge.copy(color = AppColors.DeepNavy),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper function to calculate distance between two LatLng points
private fun distance(from: LatLng, to: LatLng): Double {
    val earthRadius = 6371 // km
    val dLat = Math.toRadians(to.latitude - from.latitude)
    val dLng = Math.toRadians(to.longitude - from.longitude)
    val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(from.latitude)) *
            cos(Math.toRadians(to.latitude)) * sin(dLng / 2).pow(2.0)
    return 2 * earthRadius * atan2(sqrt(a), sqrt(1 - a))
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    DashboardScreen()
}
