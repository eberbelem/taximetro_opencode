package com.taximetro.repository

import android.content.Context
import android.content.SharedPreferences
import com.taximetro.model.PrinterConfig

class PrinterRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("printer_config", Context.MODE_PRIVATE)

    fun load(): PrinterConfig {
        return PrinterConfig(
            macAddress = prefs.getString("mac_address", "") ?: ""
        )
    }

    fun save(config: PrinterConfig) {
        prefs.edit().putString("mac_address", config.macAddress).apply()
    }
}
