package com.taximetro.gps

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GpsLogger(context: Context) {

    private val logDir = File(context.getExternalFilesDir(null), "gps_logs")

    data class GpsLogEntry(
        val timestamp: Long,
        val latitude: Double,
        val longitude: Double,
        val speedKmh: Double,
        val accuracy: Float,
        val tripId: Long
    )

    fun log(entry: GpsLogEntry) {
        try {
            if (!logDir.exists()) logDir.mkdirs()
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(entry.timestamp))
            val file = File(logDir, "gps_${dateStr}_trip_${entry.tripId}.csv")
            val header = "timestamp,latitude,longitude,speed_kmh,accuracy_m\n"
            if (!file.exists()) {
                file.writeText(header)
            }
            val line = "${entry.timestamp},${entry.latitude},${entry.longitude},${"%.2f".format(entry.speedKmh)},${"%.1f".format(entry.accuracy)}\n"
            file.appendText(line)
        } catch (_: Exception) {
        }
    }

    fun getLogFiles(): List<File> {
        return if (logDir.exists()) logDir.listFiles()?.toList() ?: emptyList() else emptyList()
    }
}
