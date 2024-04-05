package com.apextech.bexatobotuz.repository.impl

import android.util.Log
import com.apextech.bexatobotuz.data.local.database.AppDatabase
import com.apextech.bexatobotuz.data.local.entity.CyrillEntity
import com.apextech.bexatobotuz.data.local.entity.FavouriteEntity
import com.apextech.bexatobotuz.data.local.entity.LatinEntity
import com.apextech.bexatobotuz.data.remote.api.ApiService
import com.apextech.bexatobotuz.data.remote.response.Resource
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import com.apextech.bexatobotuz.repository.CyrillLatinRepository
import com.apextech.bexatobotuz.utils.Constants.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CyrillLatinRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : CyrillLatinRepository {
    override fun getCyrills(): Flow<Resource<List<WordResponse>>> {
        return flow {
            try {
                val response = apiService.getCyrills()
                if (response.isSuccessful)
                    emit(Resource.Success(response.body()!!))
                else
                    emit(Resource.Error(Throwable(response.message())))
            } catch (e: Exception) {
                emit(Resource.Error(Throwable(e.message)))
                Log.d(TAG, "getCyrills: ${e.message}")
            }
        }
    }

    override fun getLatins(): Flow<Resource<List<WordResponse>>> {
        return flow {
            val response = apiService.getLatins()
            if (response.isSuccessful)
                emit(Resource.Success(response.body()!!))
            else
                emit(Resource.Error(Throwable(response.message())))
        }
    }

    override fun getCyrillsByDatabase(): Flow<List<CyrillEntity>> {
        return appDatabase.cyrillDao().getCyrills()
    }

    override fun getLatinsByDatabase(): Flow<List<LatinEntity>> {
        return appDatabase.latinDao().getLatins()
    }

    override suspend fun insertAllCyrillsByDatabase(list: List<CyrillEntity>) {
        appDatabase.cyrillDao().insertAll(list)
    }

    override suspend fun insertAllLatinsByDatabase(list: List<LatinEntity>) {
        appDatabase.latinDao().insertAll(list)
    }

    override fun getFavouritesByDatabase(): Flow<List<FavouriteEntity>> {
        return appDatabase.favouriteDao().getFavourite()
    }

    override suspend fun insertFavouriteByDatabase(favouriteEntity: FavouriteEntity) {
        appDatabase.favouriteDao().insert(favouriteEntity)
    }

    override suspend fun deleteFavouriteByDatabase(favouriteEntity: FavouriteEntity) {
        appDatabase.favouriteDao().delete(favouriteEntity)
    }


}