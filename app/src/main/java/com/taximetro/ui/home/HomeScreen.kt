package com.taximetro.ui.home

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taximetro.ui.design.TaximetroColors

@Composable
fun HomeScreen(
    onStartTrip: (flagName: String) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Taxímetro Digital",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        GpsIndicator(gpsReady = state.gpsReady)

        Spacer(modifier = Modifier.weight(0.2f))

        Text(
            text = "Bandeirada",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
        Text(
            text = "R$ ${"%.2f".format(state.fareSettings.bandeirada)}",
            style = MaterialTheme.typography.displaySmall.copy(fontFamily = FontFamily.Monospace),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        val isFlag2 = state.currentFlag.name == "BANDEIRA_2"
        val flagColor by animateColorAsState(
            targetValue = if (isFlag2) TaximetroColors.AmberFlag2 else TaximetroColors.GreenDigital,
            animationSpec = tween(300),
            label = "flagColor"
        )
        val flagBg by animateColorAsState(
            targetValue = if (isFlag2) TaximetroColors.AmberFlag2.copy(alpha = 0.15f) else TaximetroColors.GreenDigital.copy(alpha = 0.15f),
            animationSpec = tween(300),
            label = "flagBg"
        )

        Button(
            onClick = { viewModel.toggleFlag() },
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = flagBg
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isFlag2) "BANDEIRA 2" else "BANDEIRA 1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = flagColor,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "R$ ${"%.2f".format(if (isFlag2) state.fareSettings.tarifaQuilometrica2 else state.fareSettings.tarifaQuilometrica1)}/km",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = flagColor.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "▼ tocar para alternar",
                    style = MaterialTheme.typography.labelSmall,
                    color = flagColor.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        val canStart = state.gpsReady
        val startBg by animateColorAsState(
            targetValue = if (canStart) flagColor else flagColor.copy(alpha = 0.3f),
            animationSpec = tween(300),
            label = "startBg"
        )

        Button(
            onClick = {
                if (canStart) onStartTrip(state.currentFlag.name)
            },
            enabled = canStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = startBg,
                disabledContainerColor = TaximetroColors.TextMuted.copy(alpha = 0.2f)
            )
        ) {
            Text(
                text = if (!state.gpsReady) "AGUARDANDO GPS..."
                else "INICIAR CORRIDA",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = if (canStart) Color.White else TaximetroColors.TextMuted,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(0.4f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onOpenHistory) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_myplaces),
                        contentDescription = "Histórico",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        "Histórico",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
            IconButton(onClick = onOpenSettings) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_manage),
                        contentDescription = "Configurações",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        "Config",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GpsIndicator(gpsReady: Boolean) {
    val color = if (gpsReady) TaximetroColors.GreenDigital else TaximetroColors.RedStop
    val text = if (gpsReady) "GPS OK" else "Aguardando sinal GPS..."

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}
