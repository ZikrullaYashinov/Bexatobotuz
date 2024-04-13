package com.apextech.bexatobotuz.viewModel.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apextech.bexatobotuz.data.local.entity.HistoryEntity
import com.apextech.bexatobotuz.data.model.Translator
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
import kotlin.math.ln

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

    private val _stateReplaceTranslator = MutableStateFlow(false)
    val stateReplaceTranslator = _stateReplaceTranslator.asStateFlow()

    private val _stateKeyboardMore = MutableStateFlow(false)
    val stateKeyboardMore = _stateKeyboardMore.asStateFlow()

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
        var lng = isLatin
        if (lng == null) {
            val findTranslator = findTranslator(text)
            if (findTranslator == Translator.LATIN) {
                _stateReplaceTranslator.value = true
            } else if (findTranslator == Translator.CYRILL) {
                _stateReplaceTranslator.value = false
            }
            lng = _stateReplaceTranslator.value
        }
        var translateText = ""
        if (lng) {
            val stringList = text.split(" ")
            stringList.forEach {
                val enterSplit = it.split("\n")
                for ((index, s) in enterSplit.withIndex()) {
                    translateText += translateWord(s) + if (index != enterSplit.size - 1) "\n" else ""
                }
                translateText += " "
            }
        } else {
            translateText = text
            for (word in _cyrillWords) {
                translateText = translateText.replace(word.letterCyrill, word.letterLatin)
            }
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
        viewModelScope.launch {
            if (_stateReplaceTranslator.value) {
                _stateInput.emit(historyEntity.latin)
            } else {
                _stateInput.emit(historyEntity.cyrill)
            }
        }
    }

    private fun findTranslator(text: String): Translator {
        var countCharsLatin = 0
        var countCharsCyrill = 0
        val alphabetLatinLower = "abcdefghijklmnopqrstuvwxyz"
        val alphabetCyrillLower = "абвгдежзийклмнопрстуфхцчшщъьюяқғ"
        val alphabetLatinUpper = alphabetLatinLower.uppercase()
        val alphabetCyrillUpper = alphabetLatinLower.uppercase()
        text.map {
            if (it in alphabetLatinLower || it in alphabetLatinUpper)
                countCharsLatin++
            if (it in alphabetCyrillLower || it in alphabetCyrillUpper)
                countCharsCyrill++
        }
        if (countCharsCyrill < countCharsLatin)
            return Translator.LATIN
        if (countCharsLatin < countCharsCyrill)
            return Translator.CYRILL
        return Translator.NOT_TRANSLATOR
    }

    override fun replaceTranslator() {
        _stateReplaceTranslator.value = !_stateReplaceTranslator.value
        _stateResult.value.let {
            _stateInput.value = it.trim()
            translate(it, _stateReplaceTranslator.value)
        }
    }

    override fun replaceKeyboardMore() {
        _stateKeyboardMore.value = !_stateKeyboardMore.value
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

    override fun translateWord(word: String): String {
        val regex = Regex("""(https?://\S+)|(\S+\.\S+)|(@\S+)""")
        var link = ""
        word.replace(regex) {
            link = word
            ""
        }
        if (link.isNotEmpty())
            return link

        var translateText = word
        for (w in _latinWords) {
            translateText =
                translateText.replace(w.letterLatin, w.letterCyrill)
        }
        return translateText
    }
}

sealed class TranslateResource<out T> {
    object Loading : TranslateResource<Nothing>()
    class Success<T : Any>(val data: T) : TranslateResource<T>()
    class Error(val message: String?) : TranslateResource<Nothing>()
}