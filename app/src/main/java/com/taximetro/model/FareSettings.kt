package com.taximetro.model

data class FareSettings(
    val bandeirada: Double,
    val tarifaQuilometrica1: Double,
    val tarifaQuilometrica2: Double,
    val tarifaHoraria1: Double,
    val tarifaHoraria2: Double,
    val valorFracao: Double,
    val distanciaPorFracao1: Double,
    val distanciaPorFracao2: Double
) {
    val velocidadeTransicao1: Double get() = tarifaHoraria1 / tarifaQuilometrica1
    val velocidadeTransicao2: Double get() = tarifaHoraria2 / tarifaQuilometrica2

    fun tempoPorFracao(tarifaHoraria: Double): Double {
        return (valorFracao * 3600.0) / tarifaHoraria
    }

    fun distanciaPorFracao(flag: FlagType): Double {
        return if (flag == FlagType.BANDEIRA_1) distanciaPorFracao1 else distanciaPorFracao2
    }

    fun tarifaQuilometrica(flag: FlagType): Double {
        return if (flag == FlagType.BANDEIRA_1) tarifaQuilometrica1 else tarifaQuilometrica2
    }

    fun tarifaHoraria(flag: FlagType): Double {
        return if (flag == FlagType.BANDEIRA_1) tarifaHoraria1 else tarifaHoraria2
    }
}
