package com.apextech.bexatobotuz.useCase

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.remote.response.Resource
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import kotlinx.coroutines.flow.Flow

interface TranslateUseCase {
    //    network
    fun getCyrills(): Flow<Resource<List<WordResponse>>>
    fun getLatins(): Flow<Resource<List<WordResponse>>>

    //    local database
    fun getCyrillsToDatabase(): Flow<List<WordResponse>>
    fun getLatinsToDatabase(): Flow<List<WordResponse>>
    suspend fun insertAllCyrillsToDatabase(list: List<WordResponse>)
    suspend fun insertAllLatinsToDatabase(list: List<WordResponse>)
    suspend fun insertHistoryToDatabase(historyEntity: HistoryEntity)
}