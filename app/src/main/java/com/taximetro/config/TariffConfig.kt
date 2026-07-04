package com.taximetro.config

import androidx.datastore.preferences.core.doublePreferencesKey
import com.taximetro.model.FareSettings

object TariffConfig {

    const val DEFAULT_BANDEIRADA = 5.00
    const val DEFAULT_TARIFA_KM_1 = 2.50
    const val DEFAULT_TARIFA_KM_2 = 3.25
    const val DEFAULT_TARIFA_HORARIA_1 = 30.00
    const val DEFAULT_TARIFA_HORARIA_2 = 39.00
    const val DEFAULT_VALOR_FRACAO = 0.25
    const val DEFAULT_DISTANCIA_FRACAO_1 = 150.0
    const val DEFAULT_DISTANCIA_FRACAO_2 = 115.0
    const val GPS_MIN_DISTANCE_METERS = 10.0
    const val GPS_STOP_SPEED_KMH = 1.0
    const val GPS_STOP_SECONDS = 5
    const val GPS_INTERVAL_ACTIVE_MS = 1000L
    const val GPS_INTERVAL_IDLE_MS = 5000L
    const val GPS_HDOP_MAX = 5.0f
    const val GPS_MAX_SPEED_KPH = 120.0

    val defaultFareSettings get() = FareSettings(
        bandeirada = DEFAULT_BANDEIRADA,
        tarifaQuilometrica1 = DEFAULT_TARIFA_KM_1,
        tarifaQuilometrica2 = DEFAULT_TARIFA_KM_2,
        tarifaHoraria1 = DEFAULT_TARIFA_HORARIA_1,
        tarifaHoraria2 = DEFAULT_TARIFA_HORARIA_2,
        valorFracao = DEFAULT_VALOR_FRACAO,
        distanciaPorFracao1 = DEFAULT_DISTANCIA_FRACAO_1,
        distanciaPorFracao2 = DEFAULT_DISTANCIA_FRACAO_2
    )
}
