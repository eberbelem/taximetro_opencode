package com.taximetro.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class AddressResolver(private val context: Context) {

    suspend fun resolve(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) return@withContext null
                val geocoder = Geocoder(context, Locale("pt", "BR"))
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
                addresses.firstOrNull()?.let { formatAddress(it) }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun formatAddress(address: Address): String {
        val parts = mutableListOf<String>()
        address.getAddressLine(0)?.let { parts.add(it) }
        if (parts.isEmpty()) {
            address.thoroughfare?.let { parts.add(it) }
            address.subThoroughfare?.let { if (it.isNotBlank()) parts.add(0, it) }
            address.locality?.let { parts.add(it) }
            address.adminArea?.let { parts.add(it) }
        }
        return parts.joinToString(", ")
    }
}
