package com.apextech.bexatobotuz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apextech.bexatobotuz.data.local.entity.CyrillEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CyrillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CyrillEntity>)

    @Query("SELECT * FROM cyrills")
    fun getCyrills(): Flow<List<CyrillEntity>>

}