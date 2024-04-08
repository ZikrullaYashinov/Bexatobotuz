package com.apextech.bexatobotuz.useCase

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryUseCase {
    fun getHistoriesByDatabase(): Flow<List<HistoryEntity>>
    suspend fun deleteAll(list: List<HistoryEntity>)
}