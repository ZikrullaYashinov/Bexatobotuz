package com.apextech.bexatobotuz.viewModel

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity

interface HistoryViewModel {
    fun fetchHistories()
    fun deleteAll(list: List<HistoryEntity>)
}