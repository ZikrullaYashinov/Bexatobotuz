package com.apextech.bexatobotuz.viewModel.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.remote.response.WordResponse
import com.apextech.bexatobotuz.useCase.HistoryUseCase
import com.apextech.bexatobotuz.useCase.TranslateUseCase
import com.apextech.bexatobotuz.utils.Constants.TAG
import com.apextech.bexatobotuz.viewModel.HistoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModelImpl @Inject constructor(
    private val useCase: HistoryUseCase,
) : ViewModel(), HistoryViewModel {

    private var _stateStatus = MutableStateFlow<List<HistoryEntity>>(emptyList())
    val stateStatus = _stateStatus.asStateFlow()

    init {
        fetchFavourites()
    }

    override fun fetchFavourites() {
        useCase.getFavouritesByDatabase().onEach {
            Log.d(TAG, "fetchFavourites: ${it.size}")
            _stateStatus.emit(it)
        }.launchIn(viewModelScope)
    }


}
