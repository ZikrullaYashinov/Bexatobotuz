package com.apextech.bexatobotuz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favouriteEntity: HistoryEntity)

    @Delete
    suspend fun delete(favouriteEntity: HistoryEntity)

    @Query("SELECT * FROM favourites")
    fun getFavourite(): Flow<List<HistoryEntity>>

}