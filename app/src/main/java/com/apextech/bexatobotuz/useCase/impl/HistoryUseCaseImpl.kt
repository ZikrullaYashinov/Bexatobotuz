package com.apextech.bexatobotuz.useCase.impl

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.repository.CyrillLatinRepository
import com.apextech.bexatobotuz.useCase.HistoryUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryUseCaseImpl @Inject constructor(
    private val repository: CyrillLatinRepository
) : HistoryUseCase {

    override fun getHistoriesByDatabase(): Flow<List<HistoryEntity>> {
        return repository.getHistoriesByDatabase()
    }

    override suspend fun deleteAll(list: List<HistoryEntity>) {
        list.map {
            repository.deleteHistoryByDatabase(it)
        }
    }
}