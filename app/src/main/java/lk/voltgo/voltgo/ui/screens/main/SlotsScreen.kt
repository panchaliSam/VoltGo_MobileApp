//package lk.voltgo.voltgo.ui.screens.main
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import lk.voltgo.voltgo.ui.theme.AppColors
//import lk.voltgo.voltgo.ui.viewmodel.main.SlotsViewModel
//import lk.voltgo.voltgo.data.local.entities.SlotEntity
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SlotsScreen(
//    stationId: String,
//    navController: NavHostController? = null,
//    viewModel: SlotsViewModel = hiltViewModel(),
//    onBackClick: () -> Unit = {}
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    // Trigger API call when the screen loads
//    LaunchedEffect(stationId) {
//        viewModel.loadSlots(stationId)
//    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Slots", style = MaterialTheme.typography.titleLarge) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
//                            contentDescription = "Back"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = AppColors.BrandWhite
//                )
//            )
//        },
//        containerColor = AppColors.BrandWhite
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
//            when {
//                uiState.isLoading -> {
//                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                }
//                uiState.slots.isEmpty() -> {
//                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text("No slots available")
//                    }
//                }
//                else -> {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(uiState.slots) { slot ->
//                            SlotItem(slot = slot) {
//                                // Optional: handle slot click if needed
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SlotItem(slot: SlotEntity, onClick: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(AppColors.TranslucentWhite20, shape = MaterialTheme.shapes.medium)
//            .clickable { onClick() }
//            .padding(16.dp)
//    ) {
//        Column {
//            Text("Slot: ${slot.description}", style = MaterialTheme.typography.titleMedium)
//            Text("Status: ${if (slot.isAvailable) "Available" else "Occupied"}")
//        }
//    }
//}