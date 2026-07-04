package com.taximetro.gps

import android.location.Location
import com.taximetro.config.TariffConfig
import com.taximetro.util.MathUtils

class GpsFilter {

    private var lastLocation: Location? = null
    private var lastValidTime = 0L
    private var consecutiveStops = 0

    data class FilteredResult(
        val location: Location,
        val deltaDistanceMeters: Double,
        val speedKmh: Double,
        val isMoving: Boolean
    )

    fun filter(location: Location): FilteredResult? {
        if (!location.hasAccuracy() || location.accuracy > TariffConfig.GPS_HDOP_MAX) {
            return null
        }

        val prev = lastLocation
        lastLocation = location

        if (prev == null) {
            lastValidTime = System.currentTimeMillis()
            return FilteredResult(
                location = location,
                deltaDistanceMeters = 0.0,
                speedKmh = 0.0,
                isMoving = false
            )
        }

        val distance = MathUtils.haversineDistance(
            prev.latitude, prev.longitude,
            location.latitude, location.longitude
        )

        if (distance < TariffConfig.GPS_MIN_DISTANCE_METERS) {
            return null
        }

        val timeDelta = (System.currentTimeMillis() - lastValidTime) / 1000.0
        lastValidTime = System.currentTimeMillis()

        if (timeDelta <= 0) return null

        val speed = MathUtils.speedKmh(distance, timeDelta)
        if (speed > TariffConfig.GPS_MAX_SPEED_KPH) {
            return null
        }

        val isMoving = speed >= TariffConfig.GPS_STOP_SPEED_KMH
        if (isMoving) {
            consecutiveStops = 0
        } else {
            consecutiveStops++
        }

        return FilteredResult(
            location = location,
            deltaDistanceMeters = distance,
            speedKmh = speed,
            isMoving = consecutiveStops < 2
        )
    }

    fun reset() {
        lastLocation = null
        lastValidTime = 0L
        consecutiveStops = 0
    }
}
