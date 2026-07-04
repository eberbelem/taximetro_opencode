package com.taximetro.calculation

import com.taximetro.model.FareSettings
import com.taximetro.model.FlagType
import com.taximetro.model.Trip
import com.taximetro.model.TripStatus

class TaximeterCalculator {

    private val fareCalculator = FareCalculator()
    private var state = FareCalculator.FareState()
    private var startTime = 0L
    private var lastUpdateTime = 0L

    fun startTrip(settings: FareSettings): Trip {
        val now = System.currentTimeMillis()
        startTime = now
        lastUpdateTime = now
        state = FareCalculator.FareState()

        return Trip(
            startTime = now,
            totalValue = settings.bandeirada,
            status = TripStatus.RUNNING
        )
    }

    fun update(
        deltaDistanceMeters: Double,
        speedKmh: Double,
        settings: FareSettings,
        currentFlag: FlagType
    ): Trip {
        val now = System.currentTimeMillis()
        val deltaTime = (now - lastUpdateTime) / 1000
        lastUpdateTime = now

        state = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = deltaDistanceMeters,
            deltaTimeSeconds = deltaTime,
            speedKmh = speedKmh,
            settings = settings,
            currentFlag = currentFlag
        )

        val totalTime = (now - startTime) / 1000

        return Trip(
            startTime = startTime,
            totalDistanceMeters = state.flag1DistanceMeters + state.flag2DistanceMeters,
            totalTimeSeconds = totalTime,
            idleTimeSeconds = state.idleTimeSeconds,
            flag1DistanceMeters = state.flag1DistanceMeters,
            flag2DistanceMeters = state.flag2DistanceMeters,
            flag1Fares = state.flag1Fares,
            flag2Fares = state.flag2Fares,
            totalValue = fareCalculator.calculateTotalValue(state.totalFares, settings),
            flagType = currentFlag,
            status = TripStatus.RUNNING
        )
    }

    fun finishTrip(settings: FareSettings): Trip {
        val now = System.currentTimeMillis()
        val totalTime = (now - startTime) / 1000

        return Trip(
            startTime = startTime,
            endTime = now,
            totalDistanceMeters = state.flag1DistanceMeters + state.flag2DistanceMeters,
            totalTimeSeconds = totalTime,
            idleTimeSeconds = state.idleTimeSeconds,
            flag1DistanceMeters = state.flag1DistanceMeters,
            flag2DistanceMeters = state.flag2DistanceMeters,
            flag1Fares = state.flag1Fares,
            flag2Fares = state.flag2Fares,
            totalValue = fareCalculator.calculateTotalValue(state.totalFares, settings),
            flagType = state.lastFlag,
            status = TripStatus.FINISHED
        )
    }

    fun reset() {
        state = FareCalculator.FareState()
        startTime = 0L
        lastUpdateTime = 0L
    }
}
