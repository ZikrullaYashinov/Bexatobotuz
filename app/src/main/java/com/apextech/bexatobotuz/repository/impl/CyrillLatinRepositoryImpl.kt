package com.apextech.bexatobotuz.repository.impl

import com.apextech.bexatobotuz.data.local.database.AppDatabase
import com.apextech.bexatobotuz.data.local.entity.CyrillEntity
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity
import com.apextech.bexatobotuz.data.remote.api.ApiService
import com.apextech.bexatobotuz.data.remote.response.Resource
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import com.apextech.bexatobotuz.repository.CyrillLatinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CyrillLatinRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : CyrillLatinRepository {
    override fun getCyrills(): Flow<Resource<List<WordResponse>>> {
        return flow {
            val response = apiService.getCyrills()
            if (response.isSuccessful)
                emit(Resource.Success(response.body()!!))
            else
                emit(Resource.Error(response.message()))
        }
    }

    override fun getLatins(): Flow<Resource<List<WordResponse>>> {
        return flow {
            val response = apiService.getLatins()
            if (response.isSuccessful)
                emit(Resource.Success(response.body()!!))
            else
                emit(Resource.Error(response.message()))
        }
    }

    override fun getCyrillsByDatabase(): Flow<List<CyrillEntity>> {
        return appDatabase.cyrillDao().getCyrills()
    }

    override fun getLatinsByDatabase(): Flow<List<LatinEntity>> {
        return appDatabase.latinDao().getLatins()
    }

    override suspend fun insertAllCyrillsByDatabase(list: List<CyrillEntity>) {
        appDatabase.cyrillDao().deleteCyrills()
        appDatabase.cyrillDao().insertAll(list)
    }

    override suspend fun insertAllLatinsByDatabase(list: List<LatinEntity>) {
        appDatabase.latinDao().deleteLatins()
        appDatabase.latinDao().insertAll(list)
    }

    override fun getHistoriesByDatabase(): Flow<List<HistoryEntity>> {
        return appDatabase.historyDao().getFavourite()
    }

    override suspend fun insertHistoryByDatabase(favouriteEntity: HistoryEntity) {
        appDatabase.historyDao().insert(favouriteEntity)
    }

    override suspend fun deleteHistoryByDatabase(favouriteEntity: HistoryEntity) {
        appDatabase.historyDao().delete(favouriteEntity)
    }


}