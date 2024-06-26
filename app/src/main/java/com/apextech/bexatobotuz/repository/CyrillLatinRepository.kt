package com.apextech.bexatobotuz.repository

import com.apextech.bexatobotuz.data.local.entity.CyrillEntity
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity
import com.apextech.bexatobotuz.data.remote.response.Resource
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import kotlinx.coroutines.flow.Flow

interface CyrillLatinRepository {
//    network
    fun getCyrills(): Flow<Resource<List<WordResponse>>>
    fun getLatins(): Flow<Resource<List<WordResponse>>>

//    local database
    fun getCyrillsByDatabase(): Flow<List<CyrillEntity>>
    fun getLatinsByDatabase(): Flow<List<LatinEntity>>
    suspend fun insertAllCyrillsByDatabase(list: List<CyrillEntity>)
    suspend fun insertAllLatinsByDatabase(list: List<LatinEntity>)

    fun getHistoriesByDatabase(): Flow<List<HistoryEntity>>
    suspend fun insertHistoryByDatabase(favouriteEntity: HistoryEntity)
    suspend fun deleteHistoryByDatabase(favouriteEntity: HistoryEntity)
}