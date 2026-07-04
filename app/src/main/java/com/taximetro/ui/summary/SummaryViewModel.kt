package com.taximetro.ui.summary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taximetro.TaximetroApp
import com.taximetro.model.Trip
import com.taximetro.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SummaryUiState(
    val trip: Trip? = null,
    val loading: Boolean = true
)

class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val tripRepo = TripRepository(TaximetroApp.instance.database)

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            val trip = tripRepo.getById(tripId)
            _uiState.value = SummaryUiState(trip = trip, loading = false)
        }
    }
}
