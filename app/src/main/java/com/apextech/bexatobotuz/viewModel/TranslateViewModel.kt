package com.apextech.bexatobotuz.viewModel

import com.apextech.bexatobotuz.data.local.entity.FavouriteEntity

interface TranslateViewModel {
    suspend fun fetchCyrills()
    suspend fun fetchLatins()
    fun translate(text: String)
    fun replaceTranslator()
    fun addFavourite()
    fun deleteFavourite(favouriteEntity: FavouriteEntity)

}