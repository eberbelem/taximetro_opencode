package com.taximetro.repository

import android.content.Context
import android.content.SharedPreferences
import com.taximetro.config.TariffConfig
import com.taximetro.model.FareSettings

class FareSettingsRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fare_settings", Context.MODE_PRIVATE)

    fun load(): FareSettings {
        return FareSettings(
            bandeirada = prefs.getFloat("bandeirada", TariffConfig.DEFAULT_BANDEIRADA.toFloat()).toDouble(),
            tarifaQuilometrica1 = prefs.getFloat("tarifa_km_1", TariffConfig.DEFAULT_TARIFA_KM_1.toFloat()).toDouble(),
            tarifaQuilometrica2 = prefs.getFloat("tarifa_km_2", TariffConfig.DEFAULT_TARIFA_KM_2.toFloat()).toDouble(),
            tarifaHoraria1 = prefs.getFloat("tarifa_hora_1", TariffConfig.DEFAULT_TARIFA_HORARIA_1.toFloat()).toDouble(),
            tarifaHoraria2 = prefs.getFloat("tarifa_hora_2", TariffConfig.DEFAULT_TARIFA_HORARIA_2.toFloat()).toDouble(),
            valorFracao = prefs.getFloat("valor_fracao", TariffConfig.DEFAULT_VALOR_FRACAO.toFloat()).toDouble(),
            distanciaPorFracao1 = prefs.getFloat("dist_fracao_1", TariffConfig.DEFAULT_DISTANCIA_FRACAO_1.toFloat()).toDouble(),
            distanciaPorFracao2 = prefs.getFloat("dist_fracao_2", TariffConfig.DEFAULT_DISTANCIA_FRACAO_2.toFloat()).toDouble()
        )
    }

    fun save(settings: FareSettings) {
        prefs.edit().apply {
            putFloat("bandeirada", settings.bandeirada.toFloat())
            putFloat("tarifa_km_1", settings.tarifaQuilometrica1.toFloat())
            putFloat("tarifa_km_2", settings.tarifaQuilometrica2.toFloat())
            putFloat("tarifa_hora_1", settings.tarifaHoraria1.toFloat())
            putFloat("tarifa_hora_2", settings.tarifaHoraria2.toFloat())
            putFloat("valor_fracao", settings.valorFracao.toFloat())
            putFloat("dist_fracao_1", settings.distanciaPorFracao1.toFloat())
            putFloat("dist_fracao_2", settings.distanciaPorFracao2.toFloat())
            apply()
        }
    }
}
