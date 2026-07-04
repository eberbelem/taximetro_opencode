package com.taximetro.calculation

import com.taximetro.model.FareSettings
import com.taximetro.model.FlagType

class FareCalculator {

    data class FareState(
        val totalFares: Int = 0,
        val flag1Fares: Int = 0,
        val flag2Fares: Int = 0,
        val accumulatedDistanceMeters: Double = 0.0,
        val accumulatedTimeSeconds: Long = 0,
        val flag1DistanceMeters: Double = 0.0,
        val flag2DistanceMeters: Double = 0.0,
        val idleTimeSeconds: Long = 0,
        val lastFlag: FlagType = FlagType.BANDEIRA_1
    )

    fun calculateTotalValue(fares: Int, settings: FareSettings): Double {
        return settings.bandeirada + (fares * settings.valorFracao)
    }

    fun calculateFareIncrement(
        state: FareState,
        deltaDistanceMeters: Double,
        deltaTimeSeconds: Long,
        speedKmh: Double,
        settings: FareSettings,
        currentFlag: FlagType
    ): FareState {
        if (deltaDistanceMeters <= 0 && deltaTimeSeconds <= 0) return state

        val isMoving = speedKmh >= 1.0
        val vt = if (currentFlag == FlagType.BANDEIRA_1) settings.velocidadeTransicao1 else settings.velocidadeTransicao2
        val isKmMode = speedKmh >= vt

        val newDist = state.accumulatedDistanceMeters + deltaDistanceMeters
        val newTime = state.accumulatedTimeSeconds + deltaTimeSeconds
        val distPerFracao = settings.distanciaPorFracao(currentFlag)
        val timePerFracao = settings.tempoPorFracao(settings.tarifaHoraria(currentFlag))

        var newFares = state.totalFares
        var f1Fares = state.flag1Fares
        var f2Fares = state.flag2Fares
        var distRemaining = newDist
        var timeRemaining = newTime
        var idleAdd = state.idleTimeSeconds

        if (!isMoving) {
            idleAdd += deltaTimeSeconds
        }

        if (isKmMode && isMoving) {
            val increments = (distRemaining / distPerFracao).toInt()
            if (increments > 0) {
                newFares += increments
                distRemaining -= increments * distPerFracao
                if (currentFlag == FlagType.BANDEIRA_1) f1Fares += increments else f2Fares += increments
            }
        } else if (!isKmMode) {
            val increments = (timeRemaining / timePerFracao).toInt()
            if (increments > 0) {
                newFares += increments
                timeRemaining -= (increments * timePerFracao).toLong()
                if (currentFlag == FlagType.BANDEIRA_1) f1Fares += increments else f2Fares += increments
            }
        }

        val flagDist = if (currentFlag == FlagType.BANDEIRA_1)
            state.flag1DistanceMeters + (if (isMoving) deltaDistanceMeters else 0.0)
        else
            state.flag2DistanceMeters + (if (isMoving) deltaDistanceMeters else 0.0)

        return state.copy(
            totalFares = newFares,
            flag1Fares = if (currentFlag == FlagType.BANDEIRA_1) f1Fares else state.flag1Fares,
            flag2Fares = if (currentFlag == FlagType.BANDEIRA_2) f2Fares else state.flag2Fares,
            accumulatedDistanceMeters = distRemaining,
            accumulatedTimeSeconds = timeRemaining,
            flag1DistanceMeters = if (currentFlag == FlagType.BANDEIRA_1) flagDist else state.flag1DistanceMeters,
            flag2DistanceMeters = if (currentFlag == FlagType.BANDEIRA_2) flagDist else state.flag2DistanceMeters,
            idleTimeSeconds = idleAdd,
            lastFlag = currentFlag
        )
    }
}
