package com.taximetro.map

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.compass.CompassOverlay

class MapManager(private val context: Context) {

    private var mapView: MapView? = null
    private var originMarker: Marker? = null
    private var destMarker: Marker? = null
    private var routeLine: Polyline? = null

    fun createMapView(): MapView {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            tileDownloadThreads = 2
            tileCacheTray = true
        }

        return MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setBuiltInZoomControls(false)
            controller.setZoom(16.0)
            minZoomLevel = 3.0
            maxZoomLevel = 19.0
        }.also { mapView = it }
    }

    fun updatePosition(lat: Double, lng: Double) {
        mapView?.controller?.animateTo(GeoPoint(lat, lng))
    }

    fun setOrigin(lat: Double, lng: Double, title: String = "Origem") {
        originMarker?.let { mapView?.overlays?.remove(it) }
        val marker = Marker(mapView).apply {
            position = GeoPoint(lat, lng)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title
        }
        mapView?.overlays?.add(marker)
        originMarker = marker
        mapView?.invalidate()
    }

    fun setDestination(lat: Double, lng: Double, address: String) {
        destMarker?.let { mapView?.overlays?.remove(it) }
        val marker = Marker(mapView).apply {
            position = GeoPoint(lat, lng)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = address
        }
        mapView?.overlays?.add(marker)
        destMarker = marker
        mapView?.invalidate()
    }

    fun drawRoute(points: List<GeoPoint>) {
        routeLine?.let { mapView?.overlays?.remove(it) }
        val polyline = Polyline().apply {
            setPoints(points)
            outlinePaint.color = Color.parseColor("#00C853")
            outlinePaint.strokeWidth = 8f
        }
        mapView?.overlays?.add(polyline)
        routeLine = polyline
        mapView?.invalidate()
    }

    fun onResume() {
        mapView?.onResume()
    }

    fun onPause() {
        mapView?.onPause()
    }

    fun onDetach() {
        mapView?.onDetach()
    }

    fun clear() {
        originMarker?.let { mapView?.overlays?.remove(it) }
        destMarker?.let { mapView?.overlays?.remove(it) }
        routeLine?.let { mapView?.overlays?.remove(it) }
        originMarker = null
        destMarker = null
        routeLine = null
        mapView?.invalidate()
    }
}
