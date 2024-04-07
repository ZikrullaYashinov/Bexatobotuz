package com.apextech.bexatobotuz.viewModel

import com.apextech.bexatobotuz.data.local.entity.HistoryEntity

interface HistoryViewModel {

    fun fetchFavourites()

    fun deleteAll(list: List<HistoryEntity>)

}