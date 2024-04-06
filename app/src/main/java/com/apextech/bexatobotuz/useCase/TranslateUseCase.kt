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
    fun getCyrillsByDatabase(): Flow<List<WordResponse>>
    fun getLatinsByDatabase(): Flow<List<WordResponse>>
    suspend fun insertAllCyrillsByDatabase(list: List<WordResponse>)
    suspend fun insertAllLatinsByDatabase(list: List<WordResponse>)

    fun getFavouritesByDatabase(): Flow<List<HistoryEntity>>
    suspend fun insertFavouriteByDatabase(favouriteEntity: HistoryEntity)
    suspend fun deleteFavouriteByDatabase(favouriteEntity: HistoryEntity)
}