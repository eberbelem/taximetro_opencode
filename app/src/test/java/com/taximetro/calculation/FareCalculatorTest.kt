package com.taximetro.calculation

import com.taximetro.model.FareSettings
import com.taximetro.model.FlagType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FareCalculatorTest {

    private lateinit var fareCalculator: FareCalculator
    private lateinit var settings: FareSettings

    @Before
    fun setup() {
        fareCalculator = FareCalculator()
        settings = FareSettings(
            bandeirada = 5.00,
            tarifaQuilometrica1 = 2.50,
            tarifaQuilometrica2 = 3.25,
            tarifaHoraria1 = 30.00,
            tarifaHoraria2 = 39.00,
            valorFracao = 0.25,
            distanciaPorFracao1 = 150.0,
            distanciaPorFracao2 = 115.0
        )
    }

    @Test
    fun `bandeirada is added to total value`() {
        val total = fareCalculator.calculateTotalValue(0, settings)
        assertEquals(5.00, total, 0.001)
    }

    @Test
    fun `each fare increment adds fraction value to total`() {
        val total = fareCalculator.calculateTotalValue(10, settings)
        assertEquals(7.50, total, 0.001)
    }

    @Test
    fun `total value with 20 fares`() {
        val total = fareCalculator.calculateTotalValue(20, settings)
        assertEquals(10.00, total, 0.001)
    }

    @Test
    fun `velocity transition for bandeira 1 is tarifaHoraria1 dividido por tarifaQuilometrica1`() {
        val vt = settings.velocidadeTransicao1
        assertEquals(12.0, vt, 0.001)
    }

    @Test
    fun `velocity transition for bandeira 2 is tarifaHoraria2 dividido por tarifaQuilometrica2`() {
        val vt = settings.velocidadeTransicao2
        assertEquals(12.0, vt, 0.001)
    }

    @Test
    fun `time per fraction for bandeira 1`() {
        val tpf = settings.tempoPorFracao(settings.tarifaHoraria1)
        assertEquals(30.0, tpf, 0.001)
    }

    @Test
    fun `time per fraction for bandeira 2`() {
        val tpf = settings.tempoPorFracao(settings.tarifaHoraria2)
        val expected = (0.25 * 3600) / 39.0
        assertEquals(expected, tpf, 0.001)
    }

    @Test
    fun `no increment when no distance or time`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 0.0,
            deltaTimeSeconds = 0,
            speedKmh = 0.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(0, result.totalFares)
    }

    @Test
    fun `increment by distance in km mode above transition speed`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 300.0,
            deltaTimeSeconds = 30,
            speedKmh = 36.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(2, result.totalFares)
    }

    @Test
    fun `no distance increment when speed is below transition in km mode`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 300.0,
            deltaTimeSeconds = 30,
            speedKmh = 6.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(0, result.totalFares)
    }

    @Test
    fun `increment by time in hora mode below transition speed`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 0.0,
            deltaTimeSeconds = 60,
            speedKmh = 0.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        val expectedFares = (60 / 30.0).toInt()
        assertEquals(expectedFares, result.totalFares)
    }

    @Test
    fun `accumulated distance resets after increment`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 170.0,
            deltaTimeSeconds = 30,
            speedKmh = 20.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(1, result.totalFares)
        val expectedRemaining = 170.0 - 150.0
        assertEquals(expectedRemaining, result.accumulatedDistanceMeters, 0.001)
    }

    @Test
    fun `idle time is accumulated when not moving`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 0.0,
            deltaTimeSeconds = 30,
            speedKmh = 0.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(30, result.idleTimeSeconds)
    }

    @Test
    fun `no idle time when moving`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 150.0,
            deltaTimeSeconds = 30,
            speedKmh = 18.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(0, result.idleTimeSeconds)
    }

    @Test
    fun `bandeira 2 distance per fraction is used when flag is 2`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 230.0,
            deltaTimeSeconds = 30,
            speedKmh = 28.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_2
        )
        val expectedFares = (230.0 / 115.0).toInt()
        assertEquals(expectedFares, result.totalFares)
    }

    @Test
    fun `flag2 fares are tracked separately`() {
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 230.0,
            deltaTimeSeconds = 30,
            speedKmh = 28.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_2
        )
        assertEquals(2, result.flag2Fares)
        assertEquals(0, result.flag1Fares)
    }

    @Test
    fun `multiple increments accumulate fares`() {
        var state = FareCalculator.FareState()

        state = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 300.0,
            deltaTimeSeconds = 30,
            speedKmh = 36.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(2, state.totalFares)
        assertEquals(2, state.flag1Fares)

        state = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 150.0,
            deltaTimeSeconds = 15,
            speedKmh = 36.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_2
        )
        assertEquals(3, state.totalFares)
        assertEquals(2, state.flag1Fares)
        assertEquals(1, state.flag2Fares)
    }

    @Test
    fun `time mode increments with precise fraction timing`() {
        val timePerFracao = (0.25 * 3600) / 30.0
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 0.0,
            deltaTimeSeconds = (timePerFracao * 3).toLong(),
            speedKmh = 0.0,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(3, result.totalFares)
    }

    @Test
    fun `no double counting when exactly at transition speed`() {
        val vt = settings.velocidadeTransicao1
        val state = FareCalculator.FareState()
        val result = fareCalculator.calculateFareIncrement(
            state = state,
            deltaDistanceMeters = 150.0,
            deltaTimeSeconds = (150.0 / (vt / 3.6)),
            speedKmh = vt,
            settings = settings,
            currentFlag = FlagType.BANDEIRA_1
        )
        assertEquals(1, result.totalFares)
    }

    @Test
    fun `total value calculation with mixed fares`() {
        val calculator = TaximeterCalculator()
        val trip = calculator.startTrip(settings)
        val total = trip.totalValue
        assertEquals(5.00, total, 0.001)
    }

    @Test
    fun `finish trip returns correct status`() {
        val calculator = TaximeterCalculator()
        calculator.startTrip(settings)
        val finished = calculator.finishTrip(settings)
        assertEquals(com.taximetro.model.TripStatus.FINISHED, finished.status)
    }

    @Test
    fun `reset clears calculator state`() {
        val calculator = TaximeterCalculator()
        calculator.startTrip(settings)
        calculator.finishTrip(settings)
        calculator.reset()
        val trip = calculator.startTrip(settings)
        assertEquals(5.00, trip.totalValue, 0.001)
    }
}
