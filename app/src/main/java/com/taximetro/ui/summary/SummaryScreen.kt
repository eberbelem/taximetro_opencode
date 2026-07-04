package com.taximetro.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taximetro.model.Trip
import com.taximetro.ui.design.TaximetroColors

@Composable
fun SummaryScreen(
    tripId: Long,
    onPrint: () -> Unit,
    onNewTrip: () -> Unit,
    viewModel: SummaryViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Resumo da Corrida",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        state.trip?.let { trip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "R$ ${"%.2f".format(trip.totalValue)}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = TaximetroColors.GreenDigital,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    LineItem("Bandeirada", "R$ ${"%.2f".format(trip.totalValue - (trip.flag1Fares + trip.flag2Fares) * 0.25)}")
                    if (trip.flag1Fares > 0) {
                        LineItem("Bandeira 1", "${"%.1f".format(trip.flag1DistanceMeters / 1000)} km")
                        LineItem("  Valor B1", "R$ ${"%.2f".format(trip.flag1Fares * 0.25)}")
                    }
                    if (trip.flag2Fares > 0) {
                        LineItem("Bandeira 2", "${"%.1f".format(trip.flag2DistanceMeters / 1000)} km")
                        LineItem("  Valor B2", "R$ ${"%.2f".format(trip.flag2Fares * 0.25)}")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    LineItem("Distância total", "${"%.1f".format(trip.totalDistanceMeters / 1000)} km")
                    LineItem("Tempo total", "${trip.totalTimeSeconds / 60} min")
                    LineItem("Tempo parado", "${trip.idleTimeSeconds / 60} min")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPrint,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TaximetroColors.AmberFlag2)
            ) {
                Text("Imprimir Recibo", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNewTrip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Nova Corrida", style = MaterialTheme.typography.titleMedium)
            }
        }

        if (state.loading) {
            Text("Carregando...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun LineItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
