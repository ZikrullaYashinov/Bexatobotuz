package com.apextech.bexatobotuz.viewModel.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
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
import java.util.Timer
import java.util.TimerTask
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

    private var _inputText = ""
    private var changed = false
    private var timer = Timer()

    init {
        viewModelScope.launch {
            fetchLatins()
            fetchCyrills()
        }
    }

    override suspend fun fetchCyrills() {
        if (networkHelper.isNetworkConnected()) {
            useCase.getCyrills().onEach {
                when (it) {
                    is Resource.Error -> {
                        _stateStatus.emit(TranslateResource.Error(it.message))
                    }

                    Resource.Loading -> {
                        _stateStatus.emit(TranslateResource.Loading)
                    }

                    is Resource.Success -> {
                        useCase.insertAllCyrillsToDatabase(it.data)
                    }
                }
            }.launchIn(viewModelScope)
        } else
            _stateStatus.emit(TranslateResource.Error(""))

        useCase.getCyrillsToDatabase().onEach {
            if (it.isNotEmpty()) {
                _stateStatus.emit(TranslateResource.Success(it))
                _cyrillWords = it
            }
        }.launchIn(viewModelScope)
    }

    override suspend fun fetchLatins() {
        if (networkHelper.isNetworkConnected()) {
            useCase.getLatins().onEach {
                when (it) {
                    is Resource.Error -> {
                        _stateStatus.emit(TranslateResource.Error(it.message))
                    }

                    Resource.Loading -> {
                        _stateStatus.emit(TranslateResource.Loading)
                    }

                    is Resource.Success -> {
                        useCase.insertAllLatinsToDatabase(it.data)
                    }
                }
            }.launchIn(viewModelScope)
        } else
            _stateStatus.emit(TranslateResource.Error(""))

        useCase.getLatinsToDatabase().onEach {
            if (it.isNotEmpty()) {
                _stateStatus.emit(TranslateResource.Success(it))
                _latinWords = it
            }
        }.launchIn(viewModelScope)
    }

    override fun translate(text: String, isLatin: Boolean?) {
        val lng = isLatin ?: findTranslator(text)
        _stateReplaceTranslator.value = lng
        var translateText = text
        for (word in if (!lng) _cyrillWords else _latinWords) {
            translateText = if (lng)
                translateText.replace(word.letterLatin, word.letterCyrill)
            else
                translateText.replace(word.letterCyrill, word.letterLatin)
        }
        _stateResult.value = translateText
        changed = _inputText != text
        timer.cancel()
        if (!changed) return
        _inputText = text
        timer = Timer()
        timer.schedule(
            object : TimerTask() {
                override fun run() {
                    if (text.trim().isNotEmpty())
                        addFavourite()
                }
            },
            2000
        )
    }

    override fun translateHistory(historyEntity: HistoryEntity) {
        Log.d(TAG, "translateHistory: ")
        viewModelScope.launch {
            if (_stateReplaceTranslator.value) {
                _stateInput.emit(historyEntity.latin)
            } else {
                _stateInput.emit(historyEntity.cyrill)
            }
        }
    }

    private fun findTranslator(text: String): Boolean {
        val size = text.length
        var countChars = 0
        val alphabetLower = "abcdefghijklmnopqrstuvwxyz"
        val alphabetUpper = alphabetLower.uppercase()
        text.map {
            if (it in alphabetLower || it in alphabetUpper)
                countChars++
        }
        return (size / 2) < countChars
    }

    override fun replaceTranslator() {
        _stateReplaceTranslator.value = !_stateReplaceTranslator.value
        _stateResult.value.let {
            _stateInput.value = it
            translate(it, _stateReplaceTranslator.value)
        }
    }

    override fun addFavourite() {
        viewModelScope.launch {
            if (changed) {
                val favourite =
                    if (_stateReplaceTranslator.value)
                        HistoryEntity(cyrill = _stateResult.value, latin = _inputText)
                    else
                        HistoryEntity(cyrill = _inputText, latin = _stateResult.value)
                useCase.insertHistoryToDatabase(favourite)
            }
        }
    }
}

sealed class TranslateResource<out T> {
    object Loading : TranslateResource<Nothing>()
    class Success<T : Any>(val data: T) : TranslateResource<T>()
    class Error(val message: String?) : TranslateResource<Nothing>()
}