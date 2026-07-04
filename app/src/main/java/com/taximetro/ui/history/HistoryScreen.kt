package com.taximetro.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taximetro.model.Trip
import com.taximetro.ui.design.TaximetroColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Histórico",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            androidx.compose.material3.TextButton(onClick = onBack) {
                Text("Voltar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.trips.isEmpty() && !state.loading) {
            Text(
                text = "Nenhuma corrida registrada",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.trips.filter { it.status.name == "FINISHED" }) { trip ->
                TripHistoryCard(trip = trip)
            }
        }
    }
}

@Composable
private fun TripHistoryCard(trip: Trip) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormat.format(Date(trip.startTime)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${"%.1f".format(trip.totalDistanceMeters / 1000)} km  •  ${trip.totalTimeSeconds / 60} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "R$ ${"%.2f".format(trip.totalValue)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = TaximetroColors.GreenDigital
            )
        }
    }
}
