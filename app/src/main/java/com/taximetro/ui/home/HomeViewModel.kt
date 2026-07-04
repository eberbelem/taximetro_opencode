package com.taximetro.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taximetro.config.TariffConfig
import com.taximetro.gps.GpsManager
import com.taximetro.model.FareSettings
import com.taximetro.model.FlagType
import com.taximetro.repository.FareSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val gpsReady: Boolean = false,
    val gpsSignalStrength: Float = 0f,
    val currentFlag: FlagType = FlagType.BANDEIRA_1,
    val fareSettings: FareSettings = TariffConfig.defaultFareSettings
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val gpsManager = GpsManager(application)
    private val fareRepo = FareSettingsRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        monitorGps()
    }

    private fun loadSettings() {
        _uiState.value = _uiState.value.copy(fareSettings = fareRepo.load())
    }

    private fun monitorGps() {
        viewModelScope.launch {
            gpsManager.locationFlow().collect { update ->
                _uiState.value = _uiState.value.copy(
                    gpsReady = true,
                    gpsSignalStrength = update.location.accuracy
                )
            }
        }
    }

    fun toggleFlag() {
        val current = _uiState.value.currentFlag
        _uiState.value = _uiState.value.copy(
            currentFlag = if (current == FlagType.BANDEIRA_1) FlagType.BANDEIRA_2 else FlagType.BANDEIRA_1
        )
    }
}
