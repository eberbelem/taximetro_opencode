package com.taximetro.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Receipt(
    val tripId: Long,
    val trip: Trip,
    val fareSettings: FareSettings,
    val driverName: String = "",
    val vehicleModel: String = "",
    val licensePlate: String = "",
    val taxiPrefix: String = "",
    val appVersion: String = "1.0.0"
) {
    fun formatLines(): List<String> {
        val df = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
        val timeFmt = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
        val lines = mutableListOf<String>()

        lines.add("    TAXIMETRO DIGITAL")
        lines.add("    Recibo de Corrida")
        lines.add("")

        if (driverName.isNotBlank() || taxiPrefix.isNotBlank()) {
            if (driverName.isNotBlank()) lines.add("Motorista: $driverName")
            if (taxiPrefix.isNotBlank()) lines.add("Prefixo: $taxiPrefix")
            if (vehicleModel.isNotBlank()) lines.add("Veiculo: $vehicleModel")
            if (licensePlate.isNotBlank()) lines.add("Placa: $licensePlate")
            lines.add("")
        }

        lines.add("DATA E HORA")
        lines.add("Data: ${df.format(Date(trip.startTime))}")
        trip.endTime?.let {
            lines.add("Inicio: ${timeFmt.format(Date(trip.startTime))}")
            lines.add("Termino: ${timeFmt.format(Date(it))}")
        }
        val totalMin = trip.totalTimeSeconds / 60
        val totalSec = trip.totalTimeSeconds % 60
        lines.add("Duracao: ${totalMin}min ${totalSec}s")
        lines.add("")

        lines.add("LOCALIZACAO")
        if (!trip.originAddress.isNullOrBlank()) {
            lines.add("Origem: ${trip.originAddress}")
        } else if (trip.originLat != null && trip.originLng != null) {
            lines.add("Origem: ${"%.5f".format(trip.originLat)}, ${"%.5f".format(trip.originLng)}")
        }
        if (!trip.destinationAddress.isNullOrBlank()) {
            lines.add("Destino: ${trip.destinationAddress}")
        } else if (trip.destinationLat != null && trip.destinationLng != null) {
            lines.add("Destino: ${"%.5f".format(trip.destinationLat)}, ${"%.5f".format(trip.destinationLng)}")
        }
        lines.add("")

        lines.add("TARIFAS")
        lines.add("Bandeirada: R$ ${"%.2f".format(fareSettings.bandeirada)}")
        lines.add("")

        if (trip.flag1DistanceMeters > 0) {
            lines.add("Bandeira 1:")
            lines.add("  ${"%.1f".format(trip.flag1DistanceMeters / 1000)} km")
            lines.add("  ${trip.flag1Fares}x fracoes")
            lines.add("  = R$ ${"%.2f".format(trip.flag1Fares * fareSettings.valorFracao)}")
        }
        if (trip.flag2DistanceMeters > 0) {
            lines.add("Bandeira 2:")
            lines.add("  ${"%.1f".format(trip.flag2DistanceMeters / 1000)} km")
            lines.add("  ${trip.flag2Fares}x fracoes")
            lines.add("  = R$ ${"%.2f".format(trip.flag2Fares * fareSettings.valorFracao)}")
        }
        val distKm = trip.totalDistanceMeters / 1000
        lines.add("")
        lines.add("Distancia total: ${"%.2f".format(distKm)} km")
        if (trip.idleTimeSeconds > 0) {
            val idleMin = trip.idleTimeSeconds / 60
            lines.add("Tempo parado: ${idleMin}min")
        }
        lines.add("")
        lines.add("TOTAL: R$ ${"%.2f".format(trip.totalValue)}")
        lines.add("")

        lines.add("---")
        lines.add("Taximetro Digital v$appVersion")
        lines.add("Emissao: ${df.format(Date())}")
        lines.add("")
        lines.add("Obrigado!")
        return lines
    }
}
