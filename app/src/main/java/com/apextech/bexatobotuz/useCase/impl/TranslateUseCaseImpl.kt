package com.apextech.bexatobotuz.useCase.impl

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.remote.response.Resource
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import com.apextech.bexatobotuz.repository.CyrillLatinRepository
import com.apextech.bexatobotuz.useCase.TranslateUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TranslateUseCaseImpl @Inject constructor(
    private val repository: CyrillLatinRepository
) : TranslateUseCase {

    override fun getCyrills(): Flow<Resource<List<WordResponse>>> {
        return repository.getCyrills()
    }

    override fun getLatins(): Flow<Resource<List<WordResponse>>> {
        return repository.getLatins()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCyrillsToDatabase(): Flow<List<WordResponse>> {
        return repository.getCyrillsByDatabase().flatMapConcat { cyrillEntityList ->
            return@flatMapConcat flow<List<WordResponse>> { emit(cyrillEntityList.map { it.toWordResponse() }) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLatinsToDatabase(): Flow<List<WordResponse>> {
        return repository.getLatinsByDatabase().flatMapConcat { latinEntityList ->
            return@flatMapConcat flow<List<WordResponse>> { emit(latinEntityList.map { it.toWordResponse() }) }
        }
    }

    override suspend fun insertAllCyrillsToDatabase(list: List<WordResponse>) {
        repository.insertAllCyrillsByDatabase(list.map { it.toCyrillEntity() })
    }

    override suspend fun insertAllLatinsToDatabase(list: List<WordResponse>) {
        repository.insertAllLatinsByDatabase(list.map { it.toLatinEntity() })
    }

    override suspend fun insertHistoryToDatabase(historyEntity: HistoryEntity) {
        repository.insertHistoryByDatabase(historyEntity)
    }
}