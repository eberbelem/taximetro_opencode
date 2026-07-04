package com.taximetro.ui.trip

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taximetro.TaximetroApp
import com.taximetro.calculation.TaximeterCalculator
import com.taximetro.config.TariffConfig
import com.taximetro.gps.GpsLogger
import com.taximetro.gps.GpsManager
import com.taximetro.model.FareSettings
import com.taximetro.model.FlagType
import com.taximetro.model.Trip
import com.taximetro.model.TripStatus
import com.taximetro.repository.DriverVehicleRepository
import com.taximetro.repository.FareSettingsRepository
import com.taximetro.repository.TripRepository
import com.taximetro.service.TaximeterForegroundService
import com.taximetro.util.AddressResolver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripUiState(
    val trip: Trip = Trip(),
    val fareSettings: FareSettings = TariffConfig.defaultFareSettings,
    val gpsReady: Boolean = false,
    val gpsAccuracy: Float = 0f,
    val currentLat: Double = 0.0,
    val currentLng: Double = 0.0,
    val currentSpeed: Double = 0.0,
    val elapsedSeconds: Long = 0,
    val showFinishDialog: Boolean = false,
    val gpsLost: Boolean = false
)

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val gpsManager = GpsManager(application)
    private val gpsLogger = GpsLogger(application)
    private val calculator = TaximeterCalculator()
    private val tripRepo = TripRepository(TaximetroApp.instance.database)
    private val fareRepo = FareSettingsRepository(application)
    private val dvRepo = DriverVehicleRepository(application)
    private val addressResolver = AddressResolver(application)

    private val _uiState = MutableStateFlow(TripUiState())
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    private var currentTripId = 0L
    private var currentFlag = FlagType.BANDEIRA_1
    private var startTimestamp = 0L
    private var initialized = false
    private var lastGpsUpdateTime = 0L
    private var originCaptured = false
    private var originResolveLaunched = false

    init {
        val settings = fareRepo.load()
        _uiState.value = _uiState.value.copy(fareSettings = settings)
    }

    fun init(initialFlagName: String) {
        if (initialized) return
        initialized = true
        currentFlag = if (initialFlagName == "BANDEIRA_2") FlagType.BANDEIRA_2 else FlagType.BANDEIRA_1
        val settings = _uiState.value.fareSettings
        startTimestamp = System.currentTimeMillis()
        startForegroundService()
        startTrip(settings)
    }

    private fun startForegroundService() {
        val intent = Intent(getApplication(), TaximeterForegroundService::class.java)
        getApplication<Application>().startForegroundService(intent)
    }

    private fun startTrip(settings: FareSettings) {
        val trip = calculator.startTrip(settings)
        _uiState.value = _uiState.value.copy(trip = trip.copy(flagType = currentFlag))
        viewModelScope.launch { currentTripId = tripRepo.save(trip.copy(flagType = currentFlag)) }
        monitorGps()
        startElapsedTimer()
    }

    private fun captureOrigin(lat: Double, lng: Double) {
        if (originCaptured) return
        originCaptured = true
        _uiState.value = _uiState.value.copy(
            trip = _uiState.value.trip.copy(
                originLat = lat,
                originLng = lng
            )
        )
        resolveOriginAddress(lat, lng)
    }

    private fun resolveOriginAddress(lat: Double, lng: Double) {
        if (originResolveLaunched) return
        originResolveLaunched = true
        viewModelScope.launch {
            val address = addressResolver.resolve(lat, lng)
            _uiState.value = _uiState.value.copy(
                trip = _uiState.value.trip.copy(originAddress = address)
            )
            tripRepo.save(_uiState.value.trip.copy(id = currentTripId))
        }
    }

    private fun startElapsedTimer() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                val now = System.currentTimeMillis()
                val gpsLost = lastGpsUpdateTime > 0 && (now - lastGpsUpdateTime) > 8000
                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = (now - startTimestamp) / 1000,
                    gpsLost = gpsLost
                )
            }
        }
    }

    private fun monitorGps() {
        viewModelScope.launch {
            gpsManager.locationFlow().collect { update ->
                lastGpsUpdateTime = System.currentTimeMillis()
                val lat = update.location.latitude
                val lng = update.location.longitude
                captureOrigin(lat, lng)

                val speed = if (!update.isMoving) 0.0 else update.speedKmh
                val calcTrip = calculator.update(
                    deltaDistanceMeters = update.deltaDistanceMeters,
                    speedKmh = speed,
                    settings = _uiState.value.fareSettings,
                    currentFlag = currentFlag
                )
                val currentUiTrip = _uiState.value.trip

                val mergedTrip = calcTrip.copy(
                    id = currentTripId,
                    originLat = currentUiTrip.originLat,
                    originLng = currentUiTrip.originLng,
                    originAddress = currentUiTrip.originAddress
                )

                _uiState.value = _uiState.value.copy(
                    trip = mergedTrip,
                    gpsReady = true,
                    gpsAccuracy = update.location.accuracy,
                    currentLat = lat,
                    currentLng = lng,
                    currentSpeed = speed
                )
                tripRepo.save(mergedTrip)

                gpsLogger.log(
                    GpsLogger.GpsLogEntry(
                        timestamp = System.currentTimeMillis(),
                        latitude = lat,
                        longitude = lng,
                        speedKmh = speed,
                        accuracy = update.location.accuracy,
                        tripId = currentTripId
                    )
                )
            }
        }
    }

    fun switchFlag() {
        currentFlag = if (currentFlag == FlagType.BANDEIRA_1) FlagType.BANDEIRA_2 else FlagType.BANDEIRA_1
        _uiState.value = _uiState.value.copy(
            trip = _uiState.value.trip.copy(flagType = currentFlag)
        )
    }

    fun requestFinish() {
        _uiState.value = _uiState.value.copy(showFinishDialog = true)
    }

    fun dismissFinishDialog() {
        _uiState.value = _uiState.value.copy(showFinishDialog = false)
    }

    fun finishTrip(onFinished: (Long) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showFinishDialog = false)
            val settings = _uiState.value.fareSettings
            val trip = calculator.finishTrip(settings)

            val state = _uiState.value
            val lat = state.currentLat
            val lng = state.currentLng

            val endAddress = addressResolver.resolve(lat, lng)

            val finalTrip = trip.copy(
                id = currentTripId,
                originLat = state.trip.originLat,
                originLng = state.trip.originLng,
                originAddress = state.trip.originAddress,
                destinationLat = lat,
                destinationLng = lng,
                destinationAddress = endAddress
            )
            _uiState.value = _uiState.value.copy(trip = finalTrip)
            tripRepo.save(finalTrip)
            stopForegroundService()
            stopGps()
            onFinished(currentTripId)
        }
    }

    fun cancelTrip() {
        viewModelScope.launch {
            val trip = _uiState.value.trip.copy(
                status = TripStatus.CANCELLED,
                endTime = System.currentTimeMillis()
            )
            tripRepo.save(trip.copy(id = currentTripId))
            calculator.reset()
            stopForegroundService()
            stopGps()
        }
    }

    private fun stopGps() {
        gpsManager.reset()
    }

    private fun stopForegroundService() {
        getApplication<Application>().stopService(
            Intent(getApplication(), TaximeterForegroundService::class.java)
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopForegroundService()
    }
}
