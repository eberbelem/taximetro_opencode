package com.taximetro.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taximetro.model.FlagType
import com.taximetro.model.Trip
import com.taximetro.model.TripStatus

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long?,
    val totalDistanceMeters: Double,
    val totalTimeSeconds: Long,
    val idleTimeSeconds: Long,
    val flag1DistanceMeters: Double,
    val flag2DistanceMeters: Double,
    val flag1Fares: Int,
    val flag2Fares: Int,
    val totalValue: Double,
    val flagType: String,
    val status: String,
    val originAddress: String?,
    val originLat: Double?,
    val originLng: Double?,
    val destinationAddress: String?,
    val destinationLat: Double?,
    val destinationLng: Double?,
    val createdAt: Long
) {
    fun toTrip(): Trip = Trip(
        id = id,
        startTime = startTime,
        endTime = endTime,
        totalDistanceMeters = totalDistanceMeters,
        totalTimeSeconds = totalTimeSeconds,
        idleTimeSeconds = idleTimeSeconds,
        flag1DistanceMeters = flag1DistanceMeters,
        flag2DistanceMeters = flag2DistanceMeters,
        flag1Fares = flag1Fares,
        flag2Fares = flag2Fares,
        totalValue = totalValue,
        flagType = if (flagType == "BANDEIRA_2") FlagType.BANDEIRA_2 else FlagType.BANDEIRA_1,
        status = when (status) {
            "RUNNING" -> TripStatus.RUNNING
            "FINISHED" -> TripStatus.FINISHED
            "CANCELLED" -> TripStatus.CANCELLED
            else -> TripStatus.WAITING
        },
        originAddress = originAddress,
        originLat = originLat,
        originLng = originLng,
        destinationAddress = destinationAddress,
        destinationLat = destinationLat,
        destinationLng = destinationLng,
        createdAt = createdAt
    )

    companion object {
        fun fromTrip(trip: Trip): TripEntity = TripEntity(
            id = trip.id,
            startTime = trip.startTime,
            endTime = trip.endTime,
            totalDistanceMeters = trip.totalDistanceMeters,
            totalTimeSeconds = trip.totalTimeSeconds,
            idleTimeSeconds = trip.idleTimeSeconds,
            flag1DistanceMeters = trip.flag1DistanceMeters,
            flag2DistanceMeters = trip.flag2DistanceMeters,
            flag1Fares = trip.flag1Fares,
            flag2Fares = trip.flag2Fares,
            totalValue = trip.totalValue,
            flagType = trip.flagType.name,
            status = trip.status.name,
            originAddress = trip.originAddress,
            originLat = trip.originLat,
            originLng = trip.originLng,
            destinationAddress = trip.destinationAddress,
            destinationLat = trip.destinationLat,
            destinationLng = trip.destinationLng,
            createdAt = trip.createdAt
        )
    }
}
