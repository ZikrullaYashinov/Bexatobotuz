package com.apextech.bexatobotuz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apextech.bexatobotuz.data.local.entity.LatinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LatinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<LatinEntity>)

    @Query("SELECT * FROM latins")
    fun getLatins(): Flow<List<LatinEntity>>

}