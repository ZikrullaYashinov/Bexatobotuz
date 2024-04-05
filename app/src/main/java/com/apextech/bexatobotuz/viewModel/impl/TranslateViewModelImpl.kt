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
import com.apextech.bexatobotuz.viewModel.TranslateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslateViewModelImpl @Inject constructor(
    private val useCase: TranslateUseCase,
    private val networkHelper: NetworkHelper
) : ViewModel(), TranslateViewModel {
    private var _stateStatus =
        MutableStateFlow<TranslateResource<List<WordResponse>>>(TranslateResource.Loading)
    val stateStatus = _stateStatus.asStateFlow()

    private val _stateInput = MutableStateFlow("")
    val stateInput = _stateInput.asStateFlow()

    private val _stateResult = MutableStateFlow("")
    val stateResult = _stateResult.asStateFlow()

    private val _stateReplaceTranslator = MutableStateFlow(true)
    val stateReplaceTranslator = _stateReplaceTranslator.asStateFlow()

    private var _cyrillWords = listOf<WordResponse>()
    private var _latinWords = listOf<WordResponse>()

    override suspend fun fetchCyrills() {
        if (networkHelper.isNetworkConnected()) {
            useCase.getCyrills().onEach {
                when (it) {
                    is Resource.Error -> {
                        _stateStatus.emit(TranslateResource.Error(it.e.message))
                    }

                    Resource.Loading -> {
                        _stateStatus.emit(TranslateResource.Loading)
                    }

                    is Resource.Success -> {
                        useCase.insertAllCyrillsByDatabase(it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
        useCase.getCyrillsByDatabase().onEach {
            if (it.isNotEmpty()) {
                _stateStatus.emit(TranslateResource.Success(it))
                _cyrillWords = it
                Log.d(TAG, "getCyrillsByDatabase: ${it.size}")
            } else
                _stateStatus.emit(TranslateResource.NotInternet)
        }.launchIn(viewModelScope)
    }

    override suspend fun fetchLatins() {
        _stateStatus.emit(TranslateResource.Loading)
        if (networkHelper.isNetworkConnected()) {
            useCase.getLatins().onEach {
                when (it) {
                    is Resource.Error -> {
                        _stateStatus.emit(TranslateResource.Error(it.e.message))
                    }

                    Resource.Loading -> {
                        _stateStatus.emit(TranslateResource.Loading)
                    }

                    is Resource.Success -> {
                        useCase.insertAllLatinsByDatabase(it.data)
                    }
                }
            }.launchIn(viewModelScope)
        }
        useCase.getLatinsByDatabase().onEach {
            if (it.isNotEmpty()) {
                _stateStatus.emit(TranslateResource.Success(it))
                _latinWords = it
                Log.d(TAG, "getLatinsByDatabase: ${it.size}")
            } else
                _stateStatus.emit(TranslateResource.NotInternet)
        }.launchIn(viewModelScope)
    }

    override fun translate(text: String) {
        var translateText = text
        for (word in if (!_stateReplaceTranslator.value) _cyrillWords else _latinWords) {
            translateText = if (_stateReplaceTranslator.value)
                translateText.replace(word.letterLatin, word.letterCyrill)
            else
                translateText.replace(word.letterCyrill, word.letterLatin)
        }
        _stateResult.value = translateText
    }

    override fun replaceTranslator() {
        _stateReplaceTranslator.value = !_stateReplaceTranslator.value
        _stateResult.value.let {
            _stateInput.value = it
            translate(it)
        }
    }

    override fun addFavourite() {
        viewModelScope.launch {
            val favourite =
                if (_stateReplaceTranslator.value)
                    FavouriteEntity(cyrill = _stateResult.value, latin = _stateInput.value)
                else
                    FavouriteEntity(cyrill = _stateInput.value, latin = _stateResult.value)
            useCase.insertFavouriteByDatabase(favourite)
        }
    }

    override fun deleteFavourite(favouriteEntity: FavouriteEntity) {
        viewModelScope.launch {
            useCase.deleteFavouriteByDatabase(favouriteEntity)
        }
    }

}

sealed class TranslateResource<out T> {
    object Loading : TranslateResource<Nothing>()
    object NotInternet : TranslateResource<Nothing>()
    class Success<T : Any>(val data: T) : TranslateResource<T>()
    class Error(val message: String?) : TranslateResource<Nothing>()
}