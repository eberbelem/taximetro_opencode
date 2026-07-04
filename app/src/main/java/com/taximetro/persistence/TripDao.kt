package com.taximetro.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripEntity): Long

    @Update
    suspend fun update(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getById(id: Long): TripEntity?

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips ORDER BY createdAt DESC LIMIT 50")
    suspend fun getRecent(): List<TripEntity>
}
