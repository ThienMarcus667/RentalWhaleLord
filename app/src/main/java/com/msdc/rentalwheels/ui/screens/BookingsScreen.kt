package com.msdc.rentalwheels.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun BookingScreen(
    carId: String,
    carName: String,
    onBookingConfirmed: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(10.762622, 106.660172), 12f)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Button(
                onClick = {
                    if (selectedLocation != null) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Xe đang được giao...")
                        }
                        onBookingConfirmed()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Vui lòng chọn vị trí giao xe.")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Xác nhận đặt xe")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // === Phần trên: Thông tin xe ===
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Xe: $carName", style = MaterialTheme.typography.titleLarge)
                    Text("ID: $carId", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(
                "Chọn nơi giao xe:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            // === Google Map ===
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = {
                        selectedLocation = it
                    }
                ) {
                    selectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Vị trí giao xe"
                        )
                    }
                }
            }
        }
    }
}
