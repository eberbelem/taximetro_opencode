package com.taximetro.ui.history

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

data class HistoryUiState(
    val trips: List<Trip> = emptyList(),
    val loading: Boolean = true
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val tripRepo = TripRepository(TaximetroApp.instance.database)

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tripRepo.getAllFlow().collect { trips ->
                _uiState.value = HistoryUiState(trips = trips, loading = false)
            }
        }
    }
}
