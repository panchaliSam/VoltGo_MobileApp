package lk.voltgo.voltgo.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import lk.voltgo.voltgo.ui.theme.AppColors
import lk.voltgo.voltgo.ui.viewmodel.main.StationViewModel

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
    navController: NavHostController? = null,
    viewModel: StationViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val selectedStation = remember { mutableStateOf<LatLng?>(null) }

    // Camera starts centered on Sri Lanka
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.8731, 80.7718), 7.5f)
    }

    val stations = uiState.stations

    // Filter stations by name
    val filteredStations = remember(searchQuery, stations) {
        if (searchQuery.isBlank()) stations
        else stations.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(filteredStations.size) {
        if (filteredStations.size == 1) {
            val st = filteredStations.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(st.latitude ?: 0.0, st.longitude?: 0.0), 13f
            )
            selectedStation.value = LatLng(st.latitude?: 0.0, st.longitude?: 0.0)
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(7.8731, 80.7718), 7.5f
            )
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
                .background(AppColors.BrandWhite)
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
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
                            MarkerInfoWindow(
                                state = MarkerState(
                                    position = LatLng(st.latitude?: 0.0, st.longitude?: 0.0)
                                ),
                                title = st.name,
                                snippet = st.location,
                                onInfoWindowClick = {
                                    navController?.navigate("stationDetail/${st.id}")
                                }
                            ) { _ ->
                                Column(
                                    modifier = Modifier
                                        .background(Color.White)
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        text = st.name,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
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
            .background(
                color = AppColors.TranslucentWhite20,
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
