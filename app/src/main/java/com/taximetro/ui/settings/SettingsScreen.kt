package com.taximetro.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
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
                text = "Configurações",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onBack) {
                Text("Voltar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsSection("Motorista") {
                SettingsField("Nome do motorista", state.driverName) {
                    viewModel.updateField("driverName", it)
                }
            }

            SettingsSection("Veículo") {
                SettingsField("Modelo do veículo", state.vehicleModel) {
                    viewModel.updateField("vehicleModel", it)
                }
                SettingsField("Placa", state.licensePlate) {
                    viewModel.updateField("licensePlate", it)
                }
                SettingsField("Prefixo do táxi", state.taxiPrefix) {
                    viewModel.updateField("taxiPrefix", it)
                }
            }

            SettingsSection("Impressora") {
                SettingsField("Endereço MAC Bluetooth", state.printerMac) {
                    viewModel.updateField("printerMac", it)
                }
            }

            HorizontalDivider()

            Text(
                text = "Tarifas (provisório para testes)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Na versão final, estas configurações serão definidas apenas por oficinas autorizadas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsField("Bandeirada (R$)", state.bandeirada) {
                        viewModel.updateField("bandeirada", it)
                    }
                    SettingsField("Tarifa Km B1 (R$)", state.tarifaKm1) {
                        viewModel.updateField("tarifaKm1", it)
                    }
                    SettingsField("Tarifa Km B2 (R$)", state.tarifaKm2) {
                        viewModel.updateField("tarifaKm2", it)
                    }
                    SettingsField("Tarifa Horária B1 (R$/h)", state.tarifaHora1) {
                        viewModel.updateField("tarifaHora1", it)
                    }
                    SettingsField("Tarifa Horária B2 (R$/h)", state.tarifaHora2) {
                        viewModel.updateField("tarifaHora2", it)
                    }
                    SettingsField("Valor da Fração (R$)", state.valorFracao) {
                        viewModel.updateField("valorFracao", it)
                    }
                    SettingsField("Distância/Fração B1 (m)", state.distFracao1) {
                        viewModel.updateField("distFracao1", it)
                    }
                    SettingsField("Distância/Fração B2 (m)", state.distFracao2) {
                        viewModel.updateField("distFracao2", it)
                    }
                }
            }

            Button(
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (state.saved) "Salvo!" else "Salvar Configurações")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors()
    )
}
