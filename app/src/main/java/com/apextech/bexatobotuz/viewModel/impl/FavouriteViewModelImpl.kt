package com.apextech.bexatobotuz.viewModel.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apextech.bexatobotuz.data.local.entity.FavouriteEntity
import com.apextech.bexatobotuz.data.remote.response.Resource
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import com.apextech.bexatobotuz.useCase.TranslateUseCase
import com.apextech.bexatobotuz.utils.Constants.TAG
import com.apextech.bexatobotuz.utils.NetworkHelper
import com.apextech.bexatobotuz.viewModel.FavouriteViewModel
import com.apextech.bexatobotuz.viewModel.TranslateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModelImpl @Inject constructor(
    private val useCase: TranslateUseCase,
) : ViewModel(), FavouriteViewModel {

    override fun fetchFavourites() {

    }


    override fun deleteFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            useCase.deleteFavouriteByDatabase(favouriteEntity)
        }
    }

}

sealed class FavouriteResource<out T> {
    object Loading : TranslateResource<Nothing>()
    object NotInternet : TranslateResource<Nothing>()
    class Success<T : Any>(val data: T) : TranslateResource<T>()
    class Error(val message: String?) : TranslateResource<Nothing>()
}