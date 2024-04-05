package com.apextech.bexatobotuz.viewModel

import com.apextech.bexatobotuz.data.local.entity.FavouriteEntity

interface FavouriteViewModel {

    fun fetchFavourites()
    fun deleteFavourite(favouriteEntity: FavouriteEntity)

}