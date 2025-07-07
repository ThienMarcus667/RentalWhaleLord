package com.msdc.rentalwheels.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msdc.rentalwheels.ui.theme.Typography
import com.msdc.rentalwheels.data.model.Car
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.msdc.rentalwheels.ui.components.home.CarCard
fun getCurrentDateTime(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(calendar.time)
}
@Composable
fun BrowseScreen(
    cars: List<Car>,
    favorites: Set<String>,
    onCarClick: (Car) -> Unit,
    onFavoriteClick: (Car) -> Unit,
    modifier: Modifier = Modifier,
    onFilterSelected: (String) -> Unit = {},
) {
    var selectedFilter by remember { mutableStateOf("All") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Browse",
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Date and Location
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Current date",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = getCurrentDateTime(),
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Current location",
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Buôn Ma Thuột - Đắk Lắk",
                    style = Typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
        // Filter Chips
        LazyRow(
            modifier = Modifier
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All")
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        selectedFilter = filter
                        onFilterSelected(filter)
                    },
                    label = { Text(filter) }
                )
            }
        }

        // Vehicle Count
        Text(
            text = "List Newly Updated Cars",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        // Vehicle List
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
                .weight(1f)
        ) {
            items(cars) { car ->
                CarCard(
                    car = car,
                    isFavorite = favorites.contains(car.id),
                    onFavoriteClick = { onFavoriteClick(car) },
                    onClick = { onCarClick(car) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}