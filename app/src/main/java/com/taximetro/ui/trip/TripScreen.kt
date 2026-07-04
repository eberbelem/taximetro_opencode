package com.taximetro.ui.trip

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taximetro.map.MapManager
import com.taximetro.model.FlagType
import com.taximetro.ui.design.TaximetroColors
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TripScreen(
    initialFlag: String,
    onFinishTrip: (Long) -> Unit,
    onCancel: () -> Unit,
    viewModel: TripViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(initialFlag)
    }

    if (state.showFinishDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissFinishDialog() },
            title = {
                Text("Encerrar Corrida?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Valor total: R$ ${"%.2f".format(state.trip.totalValue)}\n\n${"%.1f".format(state.trip.totalDistanceMeters / 1000)} km  •  ${state.elapsedSeconds / 60} min")
            },
            confirmButton = {
                TextButton(onClick = { viewModel.finishTrip(onFinishTrip) }) {
                    Text("ENCERRAR", color = TaximetroColors.RedStop, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissFinishDialog() }) {
                    Text("Continuar Corrida")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E14))
    ) {
        TopBar(
            gpsReady = state.gpsReady && !state.gpsLost,
            gpsAccuracy = state.gpsAccuracy,
            gpsLost = state.gpsLost
        )

        FareValueSection(totalValue = state.trip.totalValue)

        Spacer(modifier = Modifier.height(8.dp))

        FlagSwitchButton(
            currentFlag = state.trip.flagType,
            onClick = { viewModel.switchFlag() }
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoBar(
            distanceKm = state.trip.totalDistanceMeters / 1000,
            timeMinutes = state.elapsedSeconds / 60,
            speedKmh = state.currentSpeed,
            flagfall = state.fareSettings.bandeirada,
            flagType = state.trip.flagType
        )

        Spacer(modifier = Modifier.height(12.dp))

        MapPlaceholder(
            currentLat = state.currentLat,
            currentLng = state.currentLng,
            destinationAddress = state.trip.destinationAddress ?: "",
            gpsReady = state.gpsReady
        )

        Spacer(modifier = Modifier.height(12.dp))

        StopButton(onClick = { viewModel.requestFinish() })

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TopBar(gpsReady: Boolean, gpsAccuracy: Float, gpsLost: Boolean = false) {
    val now = System.currentTimeMillis()
    val timeStr = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(Date(now))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D1117))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val gpsColor = if (gpsLost) TaximetroColors.RedStop
                           else if (gpsReady) TaximetroColors.GreenDigital
                           else TaximetroColors.AmberFlag2
            val gpsText = if (gpsLost) "GPS PERDIDO"
                          else if (gpsReady) "GPS OK"
                          else "AGUARDANDO"
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(gpsColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = gpsText,
                color = gpsColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        if (gpsReady && gpsAccuracy > 0) {
            Text(
                text = "${"%.1f".format(gpsAccuracy)}m",
                color = TaximetroColors.TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Text(
            text = timeStr,
            color = TaximetroColors.TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun FareValueSection(totalValue: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E14),
                        Color(0xFF0D1B2A),
                        Color(0xFF0A0E14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "VALOR",
                color = TaximetroColors.TextMuted,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "R$ ${"%.2f".format(totalValue)}",
                color = TaximetroColors.GreenDigital,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                letterSpacing = (-1).sp
            )
        }
    }
}

@Composable
private fun FlagSwitchButton(
    currentFlag: FlagType,
    onClick: () -> Unit
) {
    val isFlag2 = currentFlag == FlagType.BANDEIRA_2
    val flagColor by animateColorAsState(
        targetValue = if (isFlag2) TaximetroColors.AmberFlag2 else TaximetroColors.GreenDigital,
        animationSpec = tween(200),
        label = "flagColor"
    )
    val flagBg by animateColorAsState(
        targetValue = if (isFlag2) TaximetroColors.AmberFlag2.copy(alpha = 0.12f) else TaximetroColors.GreenDigital.copy(alpha = 0.12f),
        animationSpec = tween(200),
        label = "flagBg"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(flagBg)
                .then(
                    Modifier
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    flagColor.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isFlag2) "B2" else "B1",
                color = flagColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Text(
            text = if (isFlag2) "BANDEIRA 2" else "BANDEIRA 1",
            color = flagColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(start = 100.dp)
                .align(Alignment.CenterStart)
        )
        Text(
            text = "▼",
            color = flagColor.copy(alpha = 0.4f),
            fontSize = 10.sp,
            modifier = Modifier
                .padding(start = 100.dp, top = 24.dp)
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun InfoBar(
    distanceKm: Double,
    timeMinutes: Long,
    speedKmh: Double,
    flagfall: Double,
    flagType: FlagType
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111822))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoItem("Distância", "${"%.1f".format(distanceKm)} km")
            InfoItem("Tempo", "${timeMinutes} min")
            InfoItem("Velocidade", "${"%.0f".format(speedKmh)} km/h")
            InfoItem("Bandeirada", "R$ ${"%.2f".format(flagfall)}")
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TaximetroColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            color = TaximetroColors.TextMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun MapPlaceholder(
    currentLat: Double,
    currentLng: Double,
    destinationAddress: String,
    gpsReady: Boolean
) {
    val context = LocalContext.current
    val mapManager = remember { MapManager(context) }

    AndroidView(
        factory = {
            mapManager.createMapView().apply {
                controller.setZoom(16.0)
                setBackgroundColor(Color.argb(255, 13, 17, 23))
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        update = { view ->
            if (gpsReady && currentLat != 0.0) {
                view.controller.animateTo(GeoPoint(currentLat, currentLng))
                if (destinationAddress.isNotEmpty()) {
                    mapManager.setDestination(currentLat, currentLng, destinationAddress)
                }
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            mapManager.onDetach()
        }
    }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = TaximetroColors.RedStop,
                disabledContainerColor = TaximetroColors.RedStop.copy(alpha = 0.4f)
            )
        ) {
            Text(
                text = "ENCERRAR CORRIDA",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
