package com.taximetro.repository

import android.content.Context
import android.content.SharedPreferences
import com.taximetro.model.DriverInfo
import com.taximetro.model.VehicleInfo

class DriverVehicleRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("driver_vehicle", Context.MODE_PRIVATE)

    fun loadDriver(): DriverInfo {
        return DriverInfo(
            name = prefs.getString("driver_name", "") ?: ""
        )
    }

    fun saveDriver(driver: DriverInfo) {
        prefs.edit().putString("driver_name", driver.name).apply()
    }

    fun loadVehicle(): VehicleInfo {
        return VehicleInfo(
            model = prefs.getString("vehicle_model", "") ?: "",
            licensePlate = prefs.getString("vehicle_plate", "") ?: "",
            taxiPrefix = prefs.getString("vehicle_prefix", "") ?: ""
        )
    }

    fun saveVehicle(vehicle: VehicleInfo) {
        prefs.edit().apply {
            putString("vehicle_model", vehicle.model)
            putString("vehicle_plate", vehicle.licensePlate)
            putString("vehicle_prefix", vehicle.taxiPrefix)
            apply()
        }
    }
}
