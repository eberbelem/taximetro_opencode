package com.taximetro.repository

import com.taximetro.model.Trip
import com.taximetro.persistence.AppDatabase
import com.taximetro.persistence.TripEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepository(private val database: AppDatabase) {

    private val dao = database.tripDao()

    suspend fun save(trip: Trip): Long {
        return if (trip.id == 0L) {
            dao.insert(TripEntity.fromTrip(trip))
        } else {
            dao.update(TripEntity.fromTrip(trip))
            trip.id
        }
    }

    suspend fun getById(id: Long): Trip? {
        return dao.getById(id)?.toTrip()
    }

    fun getByIdFlow(id: Long): Flow<Trip?> {
        return dao.getByIdFlow(id).map { it?.toTrip() }
    }

    fun getAllFlow(): Flow<List<Trip>> {
        return dao.getAllFlow().map { entities ->
            entities.map { it.toTrip() }
        }
    }
}
