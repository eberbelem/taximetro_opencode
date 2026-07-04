package com.taximetro.model

data class Trip(
    val id: Long = 0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val totalDistanceMeters: Double = 0.0,
    val totalTimeSeconds: Long = 0,
    val idleTimeSeconds: Long = 0,
    val flag1DistanceMeters: Double = 0.0,
    val flag2DistanceMeters: Double = 0.0,
    val flag1Fares: Int = 0,
    val flag2Fares: Int = 0,
    val totalValue: Double = 0.0,
    val flagType: FlagType = FlagType.BANDEIRA_1,
    val status: TripStatus = TripStatus.WAITING,
    val originAddress: String? = null,
    val originLat: Double? = null,
    val originLng: Double? = null,
    val destinationAddress: String? = null,
    val destinationLat: Double? = null,
    val destinationLng: Double? = null,
    val createdAt: Long = System.currentTimeMillis()
)
