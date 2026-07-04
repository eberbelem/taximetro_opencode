package com.taximetro.ui.receipt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taximetro.TaximetroApp
import com.taximetro.model.Receipt
import com.taximetro.repository.DriverVehicleRepository
import com.taximetro.repository.FareSettingsRepository
import com.taximetro.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReceiptUiState(
    val receipt: Receipt? = null,
    val loading: Boolean = true
)

class ReceiptViewModel(application: Application) : AndroidViewModel(application) {

    private val tripRepo = TripRepository(TaximetroApp.instance.database)
    private val fareRepo = FareSettingsRepository(application)
    private val dvRepo = DriverVehicleRepository(application)

    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    fun loadReceipt(tripId: Long) {
        viewModelScope.launch {
            val trip = tripRepo.getById(tripId)
            if (trip != null) {
                val settings = fareRepo.load()
                val driver = dvRepo.loadDriver()
                val vehicle = dvRepo.loadVehicle()
                _uiState.value = ReceiptUiState(
                    receipt = Receipt(
                        tripId = tripId,
                        trip = trip,
                        fareSettings = settings,
                        driverName = driver.name,
                        vehicleModel = vehicle.model,
                        licensePlate = vehicle.licensePlate,
                        taxiPrefix = vehicle.taxiPrefix
                    ),
                    loading = false
                )
            } else {
                _uiState.value = ReceiptUiState(loading = false)
            }
        }
    }
}
