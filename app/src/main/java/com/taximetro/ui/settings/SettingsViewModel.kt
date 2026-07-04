package com.taximetro.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.taximetro.config.TariffConfig
import com.taximetro.model.DriverInfo
import com.taximetro.model.FareSettings
import com.taximetro.model.PrinterConfig
import com.taximetro.model.VehicleInfo
import com.taximetro.repository.DriverVehicleRepository
import com.taximetro.repository.FareSettingsRepository
import com.taximetro.repository.PrinterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val driverName: String = "",
    val vehicleModel: String = "",
    val licensePlate: String = "",
    val taxiPrefix: String = "",
    val printerMac: String = "",
    val bandeirada: String = TariffConfig.DEFAULT_BANDEIRADA.toString(),
    val tarifaKm1: String = TariffConfig.DEFAULT_TARIFA_KM_1.toString(),
    val tarifaKm2: String = TariffConfig.DEFAULT_TARIFA_KM_2.toString(),
    val tarifaHora1: String = TariffConfig.DEFAULT_TARIFA_HORARIA_1.toString(),
    val tarifaHora2: String = TariffConfig.DEFAULT_TARIFA_HORARIA_2.toString(),
    val valorFracao: String = TariffConfig.DEFAULT_VALOR_FRACAO.toString(),
    val distFracao1: String = TariffConfig.DEFAULT_DISTANCIA_FRACAO_1.toString(),
    val distFracao2: String = TariffConfig.DEFAULT_DISTANCIA_FRACAO_2.toString(),
    val saved: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val fareRepo = FareSettingsRepository(application)
    private val dvRepo = DriverVehicleRepository(application)
    private val printerRepo = PrinterRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        val settings = fareRepo.load()
        val driver = dvRepo.loadDriver()
        val vehicle = dvRepo.loadVehicle()
        val printer = printerRepo.load()

        _uiState.value = SettingsUiState(
            driverName = driver.name,
            vehicleModel = vehicle.model,
            licensePlate = vehicle.licensePlate,
            taxiPrefix = vehicle.taxiPrefix,
            printerMac = printer.macAddress,
            bandeirada = settings.bandeirada.toString(),
            tarifaKm1 = settings.tarifaQuilometrica1.toString(),
            tarifaKm2 = settings.tarifaQuilometrica2.toString(),
            tarifaHora1 = settings.tarifaHoraria1.toString(),
            tarifaHora2 = settings.tarifaHoraria2.toString(),
            valorFracao = settings.valorFracao.toString(),
            distFracao1 = settings.distanciaPorFracao1.toString(),
            distFracao2 = settings.distanciaPorFracao2.toString()
        )
    }

    fun updateField(field: String, value: String) {
        _uiState.value = when (field) {
            "driverName" -> _uiState.value.copy(driverName = value, saved = false)
            "vehicleModel" -> _uiState.value.copy(vehicleModel = value, saved = false)
            "licensePlate" -> _uiState.value.copy(licensePlate = value, saved = false)
            "taxiPrefix" -> _uiState.value.copy(taxiPrefix = value, saved = false)
            "printerMac" -> _uiState.value.copy(printerMac = value, saved = false)
            "bandeirada" -> _uiState.value.copy(bandeirada = value, saved = false)
            "tarifaKm1" -> _uiState.value.copy(tarifaKm1 = value, saved = false)
            "tarifaKm2" -> _uiState.value.copy(tarifaKm2 = value, saved = false)
            "tarifaHora1" -> _uiState.value.copy(tarifaHora1 = value, saved = false)
            "tarifaHora2" -> _uiState.value.copy(tarifaHora2 = value, saved = false)
            "valorFracao" -> _uiState.value.copy(valorFracao = value, saved = false)
            "distFracao1" -> _uiState.value.copy(distFracao1 = value, saved = false)
            "distFracao2" -> _uiState.value.copy(distFracao2 = value, saved = false)
            else -> _uiState.value
        }
    }

    fun save() {
        val s = _uiState.value

        dvRepo.saveDriver(DriverInfo(name = s.driverName))
        dvRepo.saveVehicle(VehicleInfo(
            model = s.vehicleModel,
            licensePlate = s.licensePlate,
            taxiPrefix = s.taxiPrefix
        ))
        printerRepo.save(PrinterConfig(macAddress = s.printerMac))

        val settings = FareSettings(
            bandeirada = s.bandeirada.toDoubleOrNull() ?: TariffConfig.DEFAULT_BANDEIRADA,
            tarifaQuilometrica1 = s.tarifaKm1.toDoubleOrNull() ?: TariffConfig.DEFAULT_TARIFA_KM_1,
            tarifaQuilometrica2 = s.tarifaKm2.toDoubleOrNull() ?: TariffConfig.DEFAULT_TARIFA_KM_2,
            tarifaHoraria1 = s.tarifaHora1.toDoubleOrNull() ?: TariffConfig.DEFAULT_TARIFA_HORARIA_1,
            tarifaHoraria2 = s.tarifaHora2.toDoubleOrNull() ?: TariffConfig.DEFAULT_TARIFA_HORARIA_2,
            valorFracao = s.valorFracao.toDoubleOrNull() ?: TariffConfig.DEFAULT_VALOR_FRACAO,
            distanciaPorFracao1 = s.distFracao1.toDoubleOrNull() ?: TariffConfig.DEFAULT_DISTANCIA_FRACAO_1,
            distanciaPorFracao2 = s.distFracao2.toDoubleOrNull() ?: TariffConfig.DEFAULT_DISTANCIA_FRACAO_2
        )
        fareRepo.save(settings)
        _uiState.value = _uiState.value.copy(saved = true)
    }
}
