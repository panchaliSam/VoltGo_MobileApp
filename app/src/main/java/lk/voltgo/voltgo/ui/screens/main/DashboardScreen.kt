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
import androidx.hilt.navigation.compose.hiltViewModel
import lk.voltgo.voltgo.ui.screens.main.StationViewModel
import lk.voltgo.voltgo.ui.theme.AppColors

@SuppressLint("MissingPermission")
@Composable
fun DashboardScreen(
    viewModel: StationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val selectedStation = remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.8731, 80.7718), 7.5f)
    }

    // Handle search
    val stations = uiState.stations
    val filteredStations = remember(searchQuery, stations) {
        if (searchQuery.isBlank()) stations
        else stations.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Focus camera when only one station matches search
    LaunchedEffect(filteredStations.size) {
        if (filteredStations.size == 1) {
            val st = filteredStations.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(st.latitude, st.longitude), 13f
            )
            selectedStation.value = LatLng(st.latitude, st.longitude)
        }
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

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryCard("Pending", 2, AppColors.EnergyAmber)
            SummaryCard("Approved", 5, AppColors.BrandGreen)
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
                    CircularProgressIndicator(color = Color.White)
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

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.TranslucentWhite20, shape = MaterialTheme.shapes.medium)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
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

@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}