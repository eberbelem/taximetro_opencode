package com.taximetro.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapManager {

    private var googleMap: GoogleMap? = null
    private var routeLine: Polyline? = null
    private var originMarker: com.google.android.gms.maps.model.Marker? = null
    private var destMarker: com.google.android.gms.maps.model.Marker? = null

    fun init(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = false
        map.uiSettings.isCompassEnabled = true
        map.isTrafficEnabled = false
    }

    fun updatePosition(lat: Double, lng: Double) {
        val position = LatLng(lat, lng)
        googleMap?.animateCamera(
            com.google.android.gms.maps.model.CameraUpdateFactory.newLatLngZoom(position, 16f)
        )
    }

    fun setOrigin(lat: Double, lng: Double) {
        originMarker?.remove()
        originMarker = googleMap?.addMarker(
            MarkerOptions().position(LatLng(lat, lng)).title("Origem")
        )
    }

    fun setDestination(lat: Double, lng: Double, address: String) {
        destMarker?.remove()
        destMarker = googleMap?.addMarker(
            MarkerOptions().position(LatLng(lat, lng)).title(address)
        )
    }

    fun drawRoute(points: List<LatLng>) {
        routeLine?.remove()
        routeLine = googleMap?.addPolyline(
            PolylineOptions().addAll(points).color(0xFF00C853.toInt()).width(8f)
        )
    }

    fun clear() {
        originMarker?.remove()
        destMarker?.remove()
        routeLine?.remove()
        originMarker = null
        destMarker = null
        routeLine = null
    }
}
