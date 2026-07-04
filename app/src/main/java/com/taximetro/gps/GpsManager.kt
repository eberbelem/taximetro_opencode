package com.taximetro.gps

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.taximetro.config.TariffConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GpsManager(private val context: Context) {

    private val fusedLocationClient = FusedLocationProviderClient(context)
    private val filter = GpsFilter()

    data class GpsUpdate(
        val location: Location,
        val deltaDistanceMeters: Double,
        val speedKmh: Double,
        val isMoving: Boolean,
        val isValid: Boolean = true
    )

    fun locationFlow(): Flow<GpsUpdate> = callbackFlow {
        var lastUpdateTime = System.currentTimeMillis()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    lastUpdateTime = System.currentTimeMillis()
                    val filtered = filter.filter(location)
                    if (filtered != null) {
                        trySend(
                            GpsUpdate(
                                location = filtered.location,
                                deltaDistanceMeters = filtered.deltaDistanceMeters,
                                speedKmh = filtered.speedKmh,
                                isMoving = filtered.isMoving,
                                isValid = true
                            )
                        )
                    }
                }
            }
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TariffConfig.GPS_INTERVAL_ACTIVE_MS)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(TariffConfig.GPS_INTERVAL_ACTIVE_MS)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(request, callback, context.mainLooper)
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
            filter.reset()
        }
    }

    fun reset() {
        filter.reset()
    }
}
