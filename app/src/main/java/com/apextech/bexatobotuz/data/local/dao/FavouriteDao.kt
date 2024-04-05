package com.apextech.bexatobotuz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apextech.bexatobotuz.data.local.entity.FavouriteEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favouriteEntity: FavouriteEntity)

    @Delete
    suspend fun delete(favouriteEntity: FavouriteEntity)

    @Query("SELECT * FROM favourites")
    fun getFavourite(): Flow<List<FavouriteEntity>>

}